class Board {

    board = [];

    constructor() {
        for (let i = 0; i < 10; i++) {
            this.board[i] = new Array(10);
            for (let j = 0; j < 10; j++) {
                this.board[i][j] = new Location(i, j, 0);
            }
        }
    }

    update(x, y, state) {
        this.board[x][y] = state;
    }

    toString() {
        return JSON.stringify(this.board);
    }

    toJSON() {
        return JSON.parse(toString());
    }
}

class Location {
    // 0 = no ship + no hit
    // 1 = ship + no hit
    // 2 = no ship + miss
    // 4 = ship + hit

    constructor(x, y, state) {
        this.x = x;
        this.y = y;
        this.state = state;
    }
}

module.exports = Board;