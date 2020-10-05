const express = require('express');
const winston = require('winston');

const log = winston.createLogger({
    transports: [
        new winston.transports.Console({
            handleExceptions: false,
		    colorize: true,
		    timestamp: true,
		    level: 'silly',
		    json: false
        }),
        new winston.transports.File({
            level: 'silly',
			colorize: false,
			timestamp: true,
			json: false,
            filename: 'logs/system.log'
        })
    ]
});

const app = express();
const PORT = process.env.PORT || 8080

app.use('/', require('./router'));

app.listen(PORT, () => {
    log.info(`Server listening on port ${PORT}`);
});

exports.log = log;
