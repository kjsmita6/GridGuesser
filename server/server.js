const express = require('express');
const logger = require('./utils').logger;
const winston = require('winston');
const expressWinston = require('express-winston');

const app = express();
const PORT = process.env.PORT || 8080

app.use(express.json());
app.use('/', require('./router'));
app.use(expressWinston.errorLogger({
    transports: [
      new winston.transports.Console()
    ],
    colorize: true,
    json: false,
    format: winston.format.combine(
        winston.format.timestamp(),
        winston.format.colorize(),
        winston.format.printf(info => `${info.timestamp} ${info.level}: ${info.message}`)
    )
  }));

app.listen(PORT, '0.0.0.0', () => {
    logger.info(`Server listening on port ${PORT}`);
});
