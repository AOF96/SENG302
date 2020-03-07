import crypto from "crypto";

function getEncryptPassword(password){
    if(password === undefined){
        throw "Password is undefined";
    }

    return crypto.pbkdf2Sync(password, "", 1000, 64, 'sha512').toString('hex');

}

export {getEncryptPassword};