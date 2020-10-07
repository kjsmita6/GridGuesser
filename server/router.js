const express = require('express');
const router = express.Router();
const util = require('./utils');
const logger = util.logger

const DatabaseRouter = require('./database/dbrouter');
DatabaseRouter.initialize();
const database = DatabaseRouter.getInstance().db;

router.get('/', (req, res) => {
    logger.silly(util.parseReq(req));
    res.send('Success');
});

router.post('/newgame', (req, res) => {
    logger.silly(util.parseReq(req));
    let code = util.generateID();
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
    logger.silly(util.parseReq(req));
    let id = req.body.id;
    let username = req.body.username;
    database.createUser(id, username, err => {
        if (err) {
            logger.warn(`Error creating game: ${err.message}: ${err.stack}`);
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
    // TODO
});

module.exports = router;