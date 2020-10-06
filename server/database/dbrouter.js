const Database = require('./database');

class DatabaseRouter {
    
    instance = null;
    db = null;

    constructor() {
        this.db = new Database('gg');
    }

    static initialize() {
        this.instance = new DatabaseRouter();
    }

    static getInstance() {
        if (this.instance == null) {
            throw new Error('DatabaseRouter must be initialized.');
        } else {
            return this.instance;
        }
    }
}

module.exports = DatabaseRouter;