const admin = require("firebase-admin");
const serviceAccount = require('./grid-guesser-firebase-adminsdk-kamyy-a7a547de0f.json');
const logger = require('./utils').logger;

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: "https://grid-guesser.firebaseio.com"
});

class Firebase {
    static sendMessage(token, payload, options) {
        admin.messaging().sendToDevice(token, payload, options).then(response => {
            logger.silly('Successfully sent message: ' + JSON.stringify(response));
            callback(response);
        }).catch(error => {
            let err = JSON.stringify(error);
            if (err !== '{}') {
                logger.error('Error sending message: ' + JSON.stringify(error));
            }
        });
    }
}

module.exports = Firebase;
