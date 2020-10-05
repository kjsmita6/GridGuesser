const express = require('express');
const router = express.Router();
const util = require('./utils');

router.get('/', (req, res) => {
    res.send('Success');
});

router.get('/newgame', (req, res) => {
    res.send(util.generateID());
});

router.get('/newuser', (req, res) => {
    // Phone sends unique Android ID
});

router.get('/newboard', (req, res) => {
    // Create new board from what user sends, json format
});

module.exports = router;