const log = require('../utils').logger;
const sqlite3 = require('sqlite3').verbose();
const path = require('path');
const Board = require('../board');

const CREATE_TABLE_GAMES = 'create table if not exists games (id int(32) not null primary key, state int(1) not null, title varchar2(32) not null, code varchar2(4) not null, player1 varchar2(32) not null, player2 varchar2(32) null, player1_board varchar(2048) not null, player2_board varchar(2048) not null, foreign key(player1) references users(id), foreign key(player2) references users(id))';
const CREATE_TABLE_USERS = 'create table if not exists users (id varchar(32) not null primary key, username varchar(32) not null)';

class Database {
    constructor(db) {
        let p = path.resolve(__dirname, `../db/${db}.db`);
        this.db = new sqlite3.Database(p, err => {
            if (err) {
                log.error(`Error opening database ${p} -- ${err.message}: ${err.stack}`);
            } else {
                log.info(`Successfully opened database ${db}`);
            }
        });
        this.runFirstQuery(CREATE_TABLE_GAMES, [], (err, rows) => {
            if (err) {
                log.error(`Error creating games table -- ${err.message}: ${err.stack}`);
            } else {
                log.debug('Created games table');
            }
        });
        this.runFirstQuery(CREATE_TABLE_USERS, [], (err, rows) => {
            if (err) {
                log.error(`Error creating users table -- ${err.message}: ${err.stack}`);
            } else {
                log.debug('Created users table');
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

    runAllQuery(query, args, callback) {
        this.db.serialize(() => {
            this.db.all(query, args, (err, rows) => {
                callback(err, rows);
            });
        });
    }

    getGameID(callback) {
        this.runAllQuery('select * from games', [], (err, rows) => {
            if (err) {
                log.error(`Error getting gameID -- ${err.message}: ${err.stack}`);
                callback(err, null);
            } else {
                callback(null, rows.length);
            }
        });
    }

    createGame(title, code, player1, callback) {
        this.getGameID((err, id) => {
            if (err) {
                callback(err, null);
            } else {
                if (!(id >= 0)) {
                    log.verbose('gameID is -1 or null: ' + id);
                    //callback(new Error('Unable to get game ID'), null);
                } else {
                    this.runFirstQuery(`insert into games (id, state, title, code, player1, player1_board, player2_board) values (?, 0, ?, ?, ?, ?, ?)`, [id, title, code, player1, new Board(), new Board()], (err, rows) => {
                        callback(err, { id, code });
                    });
                }
            }
        });
    }

    createUser(id, username, callback) {
        this.runFirstQuery(`insert into users (id, username) values (?, ?)`, [id, username], (err, rows) => {
            callback(err);
        });
    }
}

module.exports = Database;