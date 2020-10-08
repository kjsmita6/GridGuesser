const express = require('express');
const router = express.Router();
const util = require('./utils');
const logger = util.logger

const DatabaseRouter = require('./database/dbrouter');
DatabaseRouter.initialize();
const database = DatabaseRouter.getInstance().db;

function logPost(req) {
    logger.silly(util.parseReq('POST', req));
}

router.get('/', (req, res) => {
    logger.silly(util.parseReq('GET', req));
    res.statusCode = 200;
    res.send('Success');
});

router.post('/newgame', (req, res) => {
    logPost(req);
    let code = '';
    while (code === '') {
        code = util.generateID();
        database.runAllQuery('select * from games where code = ? and (state = 1 or state = 2)', [code], (err, rows) => {
            if (err) {
                res.statusCode = 500;
                res.json({
                    error: err.message
                });
                res.end();
                return;
            }
            if (rows.length > 0) {
                code = '';
            }
        });
    }
    let title = req.body.title;
    let player1 = req.body.player1;
    database.createGame(title, code, player1, (err, response) => {
        if (err) {
            logger.warn(`Error creating game: ${err.message}: ${err.stack}`);
            res.statusCode = 500;
            res.json({
                error: err.message
            });
        } else {
            res.statusCode = 200;
            res.json({
                id: response.id,
                code: response.code
            });
        }
        res.end();
    });
});

router.post('/newuser', (req, res) => {
    logPost(req);
    let id = req.body.id;
    let username = req.body.username;
    database.createUser(id, username, err => {
        if (err) {
            logger.warn(`Error creating user: ${err.message}: ${err.stack}`);
            res.statusCode = 500;
            res.json({
                error: err.message
            });
        } else {
            res.statusCode = 200;
            res.json({ 
                error: null
            });
        }
        res.end();
    });
});

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

router.post('/joingame', (req, res) => {
    logPost(req);
    let code = req.body.code;
    let player2 = req.body.player2;
    database.getActiveGame(code, (err, rows) => {
        if (err) {
            res.statusCode = 500;
            res.json({
                error: err.message
            });
        } else {
            if (rows.length != 1) {
                res.statusCode = 500;
                res.json({
                    error: 'ERR_NO_MATCHING_GAMES'
                });
            } else {
                let id = rows[0].id;
                database.joinGame(id, player2, (err, rows) => {
                    if (err) {
                        res.statusCode = 500;
                        res.json({
                            error: err.message
                        });
                    } else {
                        res.statusCode = 200;
                    }
                });
            }
        }
        res.end();
    });
});

router.post('/finishgame', (req, res) => {
    logPost(req);
    let code = req.body.code;
    database.finishGame(code, (err, rows) => {
        if (err) {
            res.statusCode = 500;
            res.json({
                error: err.message
            });
        } else {
            res.statusCode = 200;
        }
        res.end();
    });
});

module.exports = router;