const functions = require('firebase-functions');
const admin = require('firebase-admin');

admin.initializeApp(functions.config().firebase);

 // Create and Deploy Your First Cloud Functions
 // https://firebase.google.com/docs/functions/write-firebase-functions

exports.helloWorld = functions.https.onRequest((request, response) => {
  response.send("Hello from Firebase!");
});


exports.getLike = functions.database
        .ref('/USERS/{userId}/USER_LIKES/{post}')
        .onWrite((change, context) => {
            console.log(change);
            console.log(context);
            var item = {'postId' : context.params.post, 'time' : change.after.val()};
            return admin.database().ref('/ITEMS').set(item);
        });
