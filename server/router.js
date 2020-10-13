const express = require('express');
const router = express.Router();
const util = require('./utils');
const logger = util.logger
const Firebase = require('./firebase');

const DatabaseRouter = require('./database/dbrouter');
DatabaseRouter.initialize();
const database = DatabaseRouter.getInstance().db;

function logPost(req) {
    logger.silly(util.parseReq('POST', req));
}

router.get('/', (req, res) => {
    logger.silly(util.parseReq('GET', req));
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
                    if (err1) {
                        logger.warn(`Error creating game: ${err1.message}: ${err1.stack}`);
                        res.status(500).json({ error: err1.message });
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

/*
router.post('/updateboard', (req, res) => {
    logPost(req);
    let id = req.body.id;
    let player1_update = req.body.player1_update;
    let player2_update = req.body.player2_update;
    database.getBoardsFromGame(id, (err, boards) => {
        if (err) {
            res.statusCode = 500;
            res.json({
                error: err.message
            });
        } else {
            let player1_board = JSON.parse(boards.player1_board);
            let player2_board = JSON.parse(boards.player2_board);
            player1_board[player1_update.x][player1_update.y].state = player1_update.state;
            player2_board[player2_update.x][player2_update.y].state = player2_update.state;
            database.updateBoards(id, [player1_board, player2_board], (err1, rows) => {
                if (err) {
                    res.statusCode = 500;
                    res.json({
                        error: err1.message
                    });
                } else {
                    res.statusCode = 200;
                }
            });
        }
        res.end();
    });
});
*/

router.post('/joingame', (req, res) => {
    logPost(req);
    let code = req.body.code;
    let player2 = req.body.player2;
    database.getActiveGame(code, (err, rows) => {
        if (err) {
            res.status(500).json({ error: err.message });
            return;
        } else {
            if (rows.length != 1) {
                res.status(500).json({ error: 'ERR_NO_MATCHING_GAMES' });
                return;
            } else {
                let id = rows[0].id;
                logger.verbose('Rows: ' + JSON.stringify(rows[0], null, 4));
                database.getPlayerUsername(rows[0].player1, (err1, rows1) => {
                    if (err1) {
                        res.status(500).json({ error: err1.message });
                        return;
                    } else {
                        logger.verbose('Rows1: ' + JSON.stringify(rows1, null, 4));
                        database.joinGame(id, player2, (err2, rows2) => {
                            if (err2) { 
                                res.status(500).json({ error: err2.message });
                            } else {
                                logger.verbose('Rows2: ' + rows2);
                                res.status(200).json({ error: null, id: id, title: rows[0].title, player1_username: rows1.username });
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
    database.finishGame(id, (err, rows) => {
        if (err) {
            res.status(500).json({ error: err.message });
            return;
        } else {
            res.status(200).json({ error: null });

        }
    });
});

router.post('/move', (req, res) =>{ 
    logPost(req);
    let id = req.body.id;
    logger.verbose(id);
    let player = req.body.player;
    let coords = req.body.coords;
    let x = coords.x;
    let y = coords.y;
    database.getBoards(id, (err, rows) => {
        if (err) {
            res.status(500).json({ error: err.message });
            return;
        } else {
            let player1 = rows.player1;
            let player2 = rows.player2;
            let player1_board = JSON.parse(rows.player1_board);
            let player2_board = JSON.parse(rows.player2_board);
            player1_board[x][y].state += 2;
            player2_board[x][y].state += 2;
            //logger.verbose(JSON.stringify(player1_board, null, 4));
            //logger.verbose(JSON.stringify(player2_board, null, 4));
            if (player === player1) {
                /*
                let state = player2_board[x][y].state;
                if (state == 0) {
                    logger.verbose('Player 1 miss');
                    player1_board[x][y].state = 2;
                    player2_board[x][y].state = 2;
                } else if (state == 1) {
                    logger.verbose('Player 1 hit');
                    player1_board[x][y].state = 3;
                    player2_board[x][y].state = 3;
                }
                */
                database.updateBoards(id, [JSON.stringify(player1_board), JSON.stringify(player2_board)], (err, rows) => {
                    if (err) {
                        res.status(500).json({ error: err.message });
                    } else {
                        res.status(200).json({ error: null, x: x, y: y, state: player1_board[x][y].state, turn: 2 });
                    }
               }); 
            } else if (player === player2) {
                /*
                let state = player1_board[x][y].state;
                if (state == 0) {
                    logger.verbose('Player 2 miss');
                    player1_board[x][y].state = 2;
                    player2_board[x][y].state = 2;
                } else if (state == 1) {
                    logger.verbose('Player 2 hit');
                    player1_board[x][y].state = 3;
                    player2_board[x][y].state = 3;
                }
                */
                database.updateBoards(id, [player1_board, player2_board], (err, rows) => {
                    if (err) {
                        res.status(500).json({ error: err.message });
                    } else {
                        res.status(200).json({ error: null, x: x, y: y, state: player1_board[x][y].state, turn: 1 });
                    }
                }); 
            } else {
                res.status(500).json({ error: 'ERR_MISMATCH_PLAYER' });
                return;
            }
        }
    });
});

router.post('/updateuser', (req, res) => {
    logPost(req);
    let id = req.body.id;
    let username = req.body.username;
    let token = req.body.token;
    database.updateUser(id, username, token, (err, rows) => {
        if (err) {
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
        if (err) {
            res.status(500).json({ error: err.message });
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

router.post('/makeboards', (req, res) => {
    logPost(req);
    let game = req.body.game;
    let player1_board = req.body.player1_board;
    let player2_board = req.body.player2_board;
    database.updateBoards(game, [player1_board, player2_board], (err, rows) => {
        if (err) {
            res.status(500).json({ error: err.message });
        } else {
            res.status(200).json({ error: null });
        }
    });
});

module.exports = router;