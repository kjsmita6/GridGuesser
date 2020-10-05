const log = require('../server').log;
const sqlite3 = require('sqlite3').verbose();

class Database {
    constructor(db) {
        this.log = log;
        this.db = new sqlite3.Database(`../db/${db}`, sqlite3.OPEN_CREATE | sqlite3.OPEN_READWRITE, err => {
            if (err) {
                log.error(`Error opening database ${db} -- ${err.message}: ${err.stack}`);
            } else {
                log.info(`Successfully opened database ${db}`);
            }
        });
    }

    runEachQuery(query, args, callback) {
        this.db.serialize(() => {
            this.db.each(query, args, (err, row) => {
                callback(err, row);
            });
        });
    }

    runFirstQuery(query, args, callback) {
        this.db.serialize(() => {
            this.db.get(query, args, (err, row) => {
                callback(err, row);
            });
        });
    }

    runAllQuery(query, callback) {
        this.db.serialize(() => {
            this.db.all(query, args, (err, rows) => {
                callback(err, rows);
            });
        });
    }
}

module.exports = Database;