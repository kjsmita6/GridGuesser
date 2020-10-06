const winston = require('winston');

class Util {
    static generateID() {
        let chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ';
        let result = '';
        for (let i = 0; i < 4; i++) {
            result += chars.charAt(Math.floor(Math.random() * chars.length));
        }
        return result;
    }

    static logger = winston.createLogger({
        transports: [
            new winston.transports.Console({
                handleExceptions: false,
                colorize: true,
                timestamp: true,
                level: process.argv.length == 3 ? process.argv[2] : 'info',
                json: false,
                format: winston.format.combine(winston.format.colorize(), winston.format.simple())
            }),
            new winston.transports.File({
                colorize: false,
                timestamp: true,
                level: 'debug',
                json: false,
                filename: './logs/system.log'
            })
        ]
    });

    static parseReq(req) {
        try {
            return `URL: ${JSON.stringify(req.url)}\nQuery: ${JSON.stringify(req.query)}\nHeaders: ${JSON.stringify(req.headers)}\nParams: ${JSON.stringify(req.params)}\nBody: ${JSON.stringify(req.body)}`;
        } catch (e) {
            this.logger.error(`Error parsing request -- ${e.message}: ${e.stack}`);
            return 'error parsing request';
        }
    }
}

module.exports = Util;