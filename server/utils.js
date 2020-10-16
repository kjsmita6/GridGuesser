const winston = require('winston');
winston.remove(winston.transports.Console);
const vsprintf = require('sprintf-js').vsprintf;

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
                format: winston.format.combine(winston.format.timestamp(),
                                                winston.format.colorize(),
                                                winston.format.printf(info => `${info.timestamp} ${info.level}: ${info.message}`))
            })
        ]
    });

    static parseReq(method, req) {
        try {
            return `${method}: ${JSON.stringify(req.url)}\nQuery: ${JSON.stringify(req.query)}\nHeaders: ${JSON.stringify(req.headers)}\nParams: ${JSON.stringify(req.params)}\nBody: ${JSON.stringify(req.body)}`;
        } catch (e) {
            this.logger.error(`Error parsing request -- ${e.message}: ${e.stack}`);
            return 'error parsing request';
        }
    }

    static printBoard(board) {
        board = JSON.parse(board);
        let buffer =
`\n   A||B||C||D||E||F||G||H||I||J
1 |%d||%d||%d||%d||%d||%d||%d||%d||%d||%d 
2 |%d||%d||%d||%d||%d||%d||%d||%d||%d||%d
3 |%d||%d||%d||%d||%d||%d||%d||%d||%d||%d
4 |%d||%d||%d||%d||%d||%d||%d||%d||%d||%d
5 |%d||%d||%d||%d||%d||%d||%d||%d||%d||%d
6 |%d||%d||%d||%d||%d||%d||%d||%d||%d||%d
7 |%d||%d||%d||%d||%d||%d||%d||%d||%d||%d
8 |%d||%d||%d||%d||%d||%d||%d||%d||%d||%d
9 |%d||%d||%d||%d||%d||%d||%d||%d||%d||%d
10|%d||%d||%d||%d||%d||%d||%d||%d||%d||%d`;

        let states = [];
        for (let i = 0; i < board.length; i++) {
            states[i] = new Array(10);
            for (let j = 0; j < board[i].length; j++) {
                states[i][j] = board[i][j].state;
            }
        }
        return vsprintf(buffer, [].concat.apply([], states));
    }
}

module.exports = Util;