class Board {

    board = [];

    constructor() {
        for (let i = 0; i < 10; i++) {
            this.board[i] = new Array(10);
            for (let j = 0; j < 10; j++) {
                this.board[i][j] = {
                    x: i,
                    y: j,
                    state: 0
                };
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

module.exports = Board;