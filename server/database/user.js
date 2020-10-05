const Database = require('./database');

const CREATE_TABLE = '';

class UserDB extends Database {
    
    constructor(db) {
        this.db = super(db);
        this.runFirstQuery(CREATE_TABLE, (err, rows) => {
            if (err) {
                this.log.error(`Error creating table users -- ${err.message}: ${err.stack}`);
            } else {
                this.log.info('Created users table');
            }
        });
    }
}