const Database = require('./database');

const CREATE_TABLE = '';

class GameDB extends Database {

    constructor(db) {
        this.db = super(db);
        this.runFirstQuery(CREATE_TABLE, (err, rows) => {
            if (err) {
                this.log.error(`Error creating table games -- ${err.message}: ${err.stack}`);
            } else {
                this.log.info('Created games table');
            }
        });
    }

    getGameID() {
        this.runAllQuery('select * from games', (err, rows) => {
            if (err) {
                this.log.error(`${error.message}: ${error.stack}`);
                return -1;
            } else {
                return rows.length;
            }
        });
    }

    createGame(title, code, player1) {
        this.runFirstQuery('insert into games (id, state, title, code, player1, player1_board, player2_board) values (?, 0, ?, ?, ?, ?, ?)')
    }
}