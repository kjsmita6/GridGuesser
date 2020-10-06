const express = require('express');
const logger = require('./utils').logger;

const app = express();
const PORT = process.env.PORT || 8080

app.use(express.json());
app.use('/', require('./router'));

app.listen(PORT, '0.0.0.0', () => {
    logger.info(`Server listening on port ${PORT}`);
});
