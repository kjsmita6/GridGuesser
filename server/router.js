const express = require('express');
const router = express.Router();
const util = require('./utils');
const logger = util.logger
const Firebase = require('./firebase');

const Database = require('./database/database');
const database = new Database('gg');

function logPost(req) {
    logger.silly(util.parseReq('POST', req));
}

function logGet(req) {
    logger.silly(util.parseReq('GET', req));
}

router.get('/', (req, res) => {
    logGet(req);
    res.status(200).send('Success');
    return;
});

router.post('/newgame', (req, res) => {
    logPost(req);
    let code = '';
    while (code === '') {
        code = util.generateID();
        database.runAllQuery('select * from games where code = ? and (state = 1 or state = 2)', [code], (err, rows) => {
            if (err) {
                logger.warn(`Error getting existing games -- ${err.message}: ${err.stack}`);
                res.status(500).json({ error: err.message });
                res.end();
                return;
            }
            if (rows.length > 0) {
                code = '';
            } else {
                let title = req.body.title;
                let player1 = req.body.player1;
                database.createGame(title, code, player1, (err1, response) => {
                    if (err1 || !response) {
                        logger.warn(err1 ? `Error creating game -- ${err1.message}: ${err1.stack}` : 'no rows returned');
                        res.status(500).json({ error: err1 ? err1.message : 'no rows returned' });
                        return;
                    } else {
                        res.status(200).json({ error: null, id: response.id, code: response.code });
                        return;
                    }
                });
            }
        });
    }
});

router.post('/newuser', (req, res) => {
    logPost(req);
    let id = req.body.id;
    let username = req.body.username;
    let token = req.body.token
    database.createUser(id, username, token, err => {
        if (err) {
            logger.warn(`Error creating user: ${err.message}: ${err.stack}`);
            res.status(500).json({ error: err.message });
            return;
        } else {
            res.status(200).json({ error: null });
            return;
        }
    });
});

router.post('/joingame', (req, res) => {
    logPost(req);
    let code = req.body.code;
    let player2 = req.body.player2;
    database.getActiveGame(code, (err, rows) => {
        if (err || !rows) {
            logger.warn(err ? `Error getting the active game -- ${err.message}: ${err.stack}` : 'no rows returned');
            res.status(500).json({ error: err.message });
            return;
        } else {
            if (rows.length != 1) {
                logger.warn(`No matching games`);
                res.status(500).json({ error: 'ERR_NO_MATCHING_GAMES' });
                return;
            } else {
                let id = rows[0].id;
                logger.verbose('Rows: ' + JSON.stringify(rows[0], null, 4));
                database.getPlayerUsername(rows[0].player1, (err1, rows1) => {
                    if (err1 || !rows1) {
                        logger.warn(err1 ? `Error getting player username -- ${err1.message}: ${err1.stack}` : 'no rows returned');
                        res.status(500).json({ error: err1 ? err1.message : 'no rows returned' });
                        return;
                    } else {
                        logger.verbose('Rows1: ' + JSON.stringify(rows1, null, 4));
                        database.joinGame(id, player2, (err2, rows2) => {
                            if (err2) { 
                                logger.warn(`Error joining game -- ${err2.message}: ${err2.stack}`);
                                res.status(500).json({ error: err2.message });
                            } else {
                                logger.verbose('Rows2: ' + rows2);
                                res.status(200).json({ error: null, id: id, title: rows[0].title, player1_username: rows1.username });
                                database.getUserToken(rows[0].player1, (err3, rows3) => {
                                    if (err3 || !rows3) {
                                        logger.error(err3 ? `${err3.message}: ${err3.stack}` : 'no rows returned');
                                    } else {
                                        let token = rows3.token;
                                        database.getPlayerUsername(player2, (err4, rows4) => {
                                            if (err4 || !rows4) {
                                                logger.error(err4 ? `${err4.message}: ${err4.stack}` : 'no rows returned');
                                            } else {
                                                Firebase.sendMessage(token, {
                                                    data: {
                                                        event: 'join',
                                                        id: id.toString(),
                                                        username: rows4.username
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                });
            }
        }
    });
});

router.post('/finishgame', (req, res) => {
    logPost(req);
    let id = req.body.id;
    let player = req.body.player;
    database.finishGame(id, (err, rows) => {
        if (err) {
            logger.warn(`Error finishing game -- ${err.message}: ${err.stack}`);
            res.status(500).json({ error: err.message });
            return;
        } else {
            database.getPlayersInGame(id, (err1, rows1) => {
                if (err1 || !rows1) {
                    logger.warn(err1 ? `Error getting players -- ${err1.message}: ${err1.stack}` : 'no rows returned');
                    res.status(500).json({ error: err1 ? err1.message : 'no rows returned' });
                } else {
                    res.status(200).json({ error: null });
                    if (player === rows1.player1) {
                        database.getUserToken(rows1.player2, (err2, rows2) => {
                            if (err2 || !rows2) {
                                logger.warn(err2 ? `Error getting tokens -- ${err2.message}: ${err2.stack}` : 'no rows returned');
                            } else {
                                Firebase.sendMessage(rows2.token, {
                                    data: {
                                        event: 'finish', 
                                        id: id.toString()
                                    }
                                });
                            }
                        });
                    } else if (player === rows1.player2) {
                        database.getUserToken(rows1.player1, (err2, rows2) => {
                            if (err2 || !rows2) {
                                logger.warn(err2 ? `Error getting tokens -- ${err2.message}: ${err2.stack}` : 'no rows returned');
                            } else {
                                Firebase.sendMessage(rows2.token, {
                                    data: {
                                        event: 'finish',
                                        id: id.toString()
                                    }
                                });
                            }
                        });
                    } else {
                        logger.warn(`Unknown player ${player}`);
                    }
                }
            });
        }
    });
});

router.post('/move', (req, res) =>{ 
    logPost(req);
    let id = req.body.id;
    logger.verbose('id: ' + id);
    let coords = req.body.coords;
    let player = req.body.player;
    let x = coords.x;
    let y = coords.y;
    database.getBoards(id, (err, rows) => {
        if (err || !rows) {
            logger.warn(err ? `Error getting boards -- ${err.message}: ${err.stack}` : 'no rows returned');
            res.status(500).json({ error: err ? err.message : 'no rows returned' });
            return;
        } else {
            let player1 = rows.player1;
            let player2 = rows.player2;
            let player1_board = JSON.parse(rows.player1_board);
            let player2_board = JSON.parse(rows.player2_board);
            let turn = ~rows.turn && 0x3;
            if (rows.turn === 1) {
                turn = 2;
            } else if (rows.turn === 2) {
                turn = 1;
            }
            if (player === player1) {
                player2_board[x][y].state += 2;
            } else if (player === player2) {
                player1_board[x][y].state += 2;
            }
            database.updateBoards(id, [JSON.stringify(player1_board), JSON.stringify(player2_board)], turn, (err1, rows1) => {
                if (err1) {
                    logger.warn(`Error getting boards -- ${err1.message}: ${err1.stack}`);
                    res.status(500).json({ error: err1.message });
                } else {
                    res.status(200).json({ error: null, x: x, y: y, state: player === player1 ? player2_board[x][y].state : player1_board[x][y].state, turn: turn });
                    database.getPlayersInGame(id, (err2, rows2) => {
                        if (err2 || !rows2) {
                            logger.warn(err2 ? `Error getting players -- ${err2.message}: ${err2.stack}` : 'no rows returned');
                        } else {
                            logger.verbose('rows2: ' + JSON.stringify(rows2, null, 4));
                            database.getUserToken(turn === 2 ? rows2.player2 : rows2.player1, (err3, rows3) => {
                                if (err3 || !rows3) {
                                    logger.warn(err3 ? `Error getting player token -- ${err3.message}: ${err3.stack}` : 'no rows returned');    
                                } else {
                                    Firebase.sendMessage(rows3.token, {
                                        data: {
                                            event: 'turn',
                                            id: id.toString(),
                                            state: player === player1 ? player2_board[x][y].state.toString() : player1_board[x][y].state.toString()
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            }); 
        }
    });
});

router.post('/updateuser', (req, res) => {
    logPost(req);
    let id = req.body.id;
    let username = req.body.username;
    let token = req.body.token;
    //database.updateUser(id, username, token, (err, rows) => {
    database.insertOrUpdateUser(id, username, token, (err, rows) => { 
        if (err) {
            logger.warn(`Error updating/creating user -- ${err.message}: ${err.stack}`);
            res.status(500).json({ error: err.message });
        } else {
            res.status(200).json({ error: null });
        }
    });
});

router.post('/testmessage', (req, res) => {
    logPost(req);
    let token = 'd_fp2RkTR1-7A8_s-b0d1E:APA91bELxfY8ur0npmWA79kGSdx0UGSUWC6gAul-SGaKhYY29Y0lT4sbNCGcpfMMo_1dJn3DjOF69yiZSABt9tDMOhLHN8F0nuKHO2Fhtji8wZh1nWGW70jKpgSKmVCaUyGFYON_UVBe';
    let payload = req.body.payload
    logger.verbose(JSON.stringify(payload, null, 4));
    let options = {
        priority: 'high'
    };
    Firebase.sendMessage(token, payload, options);
    res.status(200).end();
});

router.post('/whichplayer', (req, res) => {
    logPost(req);
    let game = req.body.game;
    let player = req.body.player;
    database.getPlayersInGame(game, (err, rows) => {
        if (err || !rows) {
            logger.warn(err ? `Error getting which player -- ${err.message}: ${err.stack}` : 'no rows returned');
            res.status(500).json({ error: err ? err.message : 'no rows returned' });
        } else {
            if (player === rows.player1) {
                res.status(200).json({ error: null, player: 1 });
            } else if (player === rows.player2) {
                res.status(200).json({ error: null, player: 2 });
            } else {
                logger.warn('Unknown player ' + player);
                res.status(500).json({ error: 'Unknown player ' + player });
            }
        }
    });
});

router.post('/makeboard', (req, res) => {
    logPost(req);
    let game = req.body.game;
    let player = req.body.player;
    let board = req.body.board;
    database.getPlayersInGame(game, (err, rows) => {
        if (err || !rows) {
            logger.warn(err ? `Error getting players in game -- ${err.message}: ${err.stack}` : 'no rows returned');
            res.status(500).json({ error: err ? err.message : 'no rows returned' });
        } else {
            database.updateBoard(game, player === rows.player1 ? 1 : 2, board, (err1, rows1) => {
                if (err1) {
                    res.status(500).json({ error: err1.message });
                } else {
                    res.status(200).json({ error: null });
                    database.getUserToken(player === rows.player1 ? rows.player2 : rows.player1, (err3, rows3) => {
                        if (err3 || !rows3) {
                            logger.warn(err2 ? `Error getting player token -- ${err3.message}: ${err3.stack}` : 'no rows returned');    
                        } else {
                            Firebase.sendMessage(rows3.token, {
                                data: {
                                    event: 'board',
                                    id: game.toString()
                                }
                            });
                        }
                    });
                }
            });
        }
    });
});

router.post('/getboards', (req, res) => {
    logPost(req);
    let game = req.body.game;
    database.getBoards(game, (err, rows) => {
        if (err || !rows) {
            logger.warn(err ? `Error getting boards -- ${err.message}: ${err.stack}` : 'no rows returned');
            res.status(500).json({ error: err ? err.message : 'no rows returned' });
        } else {
            res.status(200).json({
                turn: rows.turn,
                player1: rows.player1,
                player1_board: rows.player1_board,
                player2: rows.player2,
                player2_board: rows.player2_board
            });
        }
    });
});

module.exports = router;