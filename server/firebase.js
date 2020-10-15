const admin = require("firebase-admin");
const serviceAccount = require('./grid-guesser-firebase-adminsdk-kamyy-a7a547de0f.json');
const logger = require('./utils').logger;

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: "https://grid-guesser.firebaseio.com"
});

class Firebase {
    static sendMessage(token, payload, options) {
        if (!options) {
            options = {
                priority: 'normal'
            }
        }
        admin.messaging().sendToDevice(token, payload, options).then(response => {
            logger.silly('Successfully sent message: ' + JSON.stringify(response));
            callback(response);
        }).catch(error => {
            if (error) {
                logger.error('Error sending message: ' + JSON.stringify(error, null, 4));
            }
        });
    }
}

module.exports = Firebase;
