const express = require('express');
const router = express.Router();
const util = require('./utils');
const logger = util.logger

const DatabaseRouter = require('./database/dbrouter');
DatabaseRouter.initialize();
const database = DatabaseRouter.getInstance();


router.get('/', (req, res) => {
    logger.silly(util.parseReq(req));
    res.send('Success');
});

router.post('/newgame', (req, res) => {
    logger.silly(util.parseReq(req));
    let code = util.generateID();
    let title = req.body.title;
    let player1 = req.body.player1;
    database.db.createGame(title, code, player1, (err, response) => {
        if (err) {
            logger.warn(`error: ${err.message}: ${err.stack}`);
            res.statusCode = 500;
            res.statusMessage = err.message;
        } else {
            logger.verbose('error: ' + JSON.stringify(err));
            logger.verbose('response: ' + JSON.stringify(response));
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
    // Phone sends unique Android ID
});

router.post('/newboard', (req, res) => {
    logger.silly(util.parseReq(req));
    // Create new board from what user sends, json format
});

module.exports = router;