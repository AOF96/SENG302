export const userInfo = {
    profileId: null,
    firstname: null,
    lastname:null,
    middlename:null,
    nickname:null,
    gender:null,
    email:null,
    secondaryEmails: [],
    birthday:null,
    bio: null,
    isLogin: false
};

export const helper = {
  getCookie: (name) => {
    var value = "; " + document.cookie;
    var parts = value.split("; " + name + "=");
    if (parts.length == 2) return parts.pop().split(";").shift();
  }
};
