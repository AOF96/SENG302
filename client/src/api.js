import axios from "axios";

const SERVER_URL = process.env.VUE_APP_SERVER_ADD;

var instance = axios.create({
  baseURL: SERVER_URL,
  timeout: 5000000,
  withCredentials: true,
});

/**
 * Replace spaces in activity types with hyphens
 * @param activities
 * @returns list of activity types
 */
function activitiesAddDashes(activities) {
  // Replace all spaces in each activity type with "-" to support the json spec
  let parsedActivityTypes = [];
  for (let i = 0; i < activities.length; i++) {
    parsedActivityTypes.push(activities[i].split(' ').join('-'));
  }
  return parsedActivityTypes;
}

export const apiUser = {
  /**
   * Send request to change user password
   */
  changePassword: (profile_id, old_password, new_password, repeat_password) =>
    instance.put("/profiles/" + profile_id + "/password", {
      old_password: old_password,
      new_password: new_password,
      repeat_password: repeat_password,
    }),

  /**
   * Send request to sign up new user account
   */
  signUp: (
    firstname,
    lastname,
    middlename,
    nickname,
    primary_email,
    password,
    bio,
    date_of_birth,
    gender,
    fitness
  ) =>
    instance.post("/profiles", {
      firstname: firstname,
      lastname: lastname,
      middlename: middlename,
      nickname: nickname,
      primary_email: primary_email,
      password: password,
      bio: bio,
      date_of_birth: date_of_birth,
      gender: gender,
      fitness: fitness,
    }),

  /**
   * Send request to log in existing user
   */
  login: (email, password) =>
    instance.post("/login", {
      email: email,
      password: password,
    }),

  /**
   * Removes session token from local storage and posts server request to remove the token from the database
   */
  logout: () => instance.post("/logout"),

  /**
   * Send request to edit user profile
   */
  editProfile: (
    profile_id,
    firstname,
    lastname,
    middlename,
    nickname,
    primary_email,
    bio,
    date_of_birth,
    gender,
    fitness,
    additional_email,
    passports,
    permission_level,
    activities,
    location
  ) =>
    instance.put("/profiles/" + profile_id, {
      firstname: firstname,
      lastname: lastname,
      middlename: middlename,
      nickname: nickname,
      primary_email: primary_email,
      bio: bio,
      date_of_birth: date_of_birth,
      gender: gender,
      fitness: fitness,
      additional_email: additional_email,
      passports: passports,
      permission_level: permission_level,
      activities: activities,
      location: location,
    }),

  /**
   * Sends request to get a users information
   */
  refreshUserData: (profile_id) => instance.get("/profiles/" + profile_id),

  /**
   * Send request to add additional emails to a users profile
   */
  addEmails: (profile_id, additional_email) =>
    instance.post("/profiles/" + profile_id + "/emails", {
      additional_email: additional_email,
    }),

  /**
   * Send request to edit a users emails
   */
  editEmail: (profile_id, primary_email, additional_email) =>
    instance.put("/profiles/" + profile_id + "/emails", {
      primary_email: primary_email,
      additional_email: additional_email,
    }),

  /**
   * Send request to retrieve all of a users emails
   */
  getAllEmails: () => instance.get('/emails'),

  /**
   * Send request to get a users id from their email
   */
  getIdByEmail: (email) => instance.get('/email/id/', {
    params:
      {email: email}
  }),

  /**
   * Get the session token for a user
   */
  getUserSessionToken: (profile_id) => instance.get('/token/' + profile_id),

  /**
   * Sends request to get a user from their session token
   */
  getUserByToken: () => instance.get('validateLogin'),

  /**
   * Sends a request to get a user from their id
   */
  async getUserById(profile_id) {
    let searchedUser = await apiUser.refreshUserData(profile_id).then(
      (response) => {
        return response.data;
      },
      (error) => {
        if (error) {
          return "Invalid permissions";
        }
      }
    );
    return searchedUser;
  },

  /**
   * Sends a search request for users
   */
  searchUsers: (searchTerm, searchType, activityTypes, searchTerms, searchTypes, searchTermsMethod, method, page, size) => instance.get('/profiles/',
    {
      params: {
        [searchType]: searchTerm,
        method: method,
        activity: activitiesAddDashes(activityTypes).join(" "),
          searchTerms: searchTerms.join(" "),
          searchTypes: searchTypes,
          searchTermsMethod: searchTermsMethod,
        page: page,
        size: size,
      }
    }),

  /**
   * Parses and sends a search request from a search object
   */
  searchedUser(searchedTerm, searchFilter) {
    let filter = {};
    filter[searchFilter] = searchedTerm;
    filter['method'] = 'OR';
    filter['page'] = 0;
    filter['size'] = 3;

    apiUser.searchUsers(filter).then(
        response => {
          return response.data;
        },
        error => {
          if (error) {
            return "Invalid permissions";
          }
        }
    );
  },

  /**
   * Sends a request to retrieve all of a users continuous activities
   */
  getUserContinuousActivities: (profile_id) => instance.get('/profiles/' + profile_id + '/activities/continuous'),

  /**
   * Sends a request to retrieve all of a users duration activities
   */
  getUserDurationActivities: (profile_id) => instance.get('/profiles/' + profile_id + '/activities/duration'),

  /**
   * Request to get all activity types from the server
   */
  getActivityTypes: () => instance.get('/activity-types'),

  /**
   * Request to update activity types
   */
  editUserActivityTypes: (profile_id, activities) =>
    instance.put("/profiles/" + profile_id + "/activity-types", {
      activities: activitiesAddDashes(activities),
    }),

  /***
   * API call to promote an user with permission level 0 to admin.
   * @param profile_id the id of the user being promoted.
   */
  promoteToAdmin: (profile_id) =>
    instance.put("/profiles/" + profile_id + "/role", {
      "role": "admin"
    }),

  /**
   * Sends a request to delete a users account
   */
  deleteUserAccount: (profile_id) => instance.delete(`profiles/${profile_id}`),

  /**
   * Makes a request to update a user's role for an activity
   * @param profileId the id of the user
   * @param activityId the id of the activity
   * @param email the email of the user whose role requires updating
   * @param activityRole the user's role for the activity ie follower, participant, organiser or creator
   */
  editUserRoleForActivity: (profileId, activityId, email, activityRole) =>
    instance.put(`/profiles/${profileId}/activities/${activityId}/subscriber`,
    {
        email: email,
        activityRole: activityRole
    }),

  /**
   * Send a request to check if a user is following an activity
   * @param userId
   * @param activityId
   */
  isUserFollowingActivity: (userId, activityId) => instance.get('/profiles/' + userId + '/subscriptions/activities/' + activityId),

  /**
   * API call to retrieve the home feed details for a user
   * @param profileId the id of the user that requires feed retrieval
   * @param page the page number
   * @param size how many posts we want for each page
   */
  getUserFeed: (profileId, page, size) => instance.get(`/profiles/${profileId}/feed`,
  {
      params: {
          page: page,
          size: size
      }
  }),

  /**
   *Sends a request to the server to update the given user location
   * @param profileId the id of the user being updated
   * @param location the json object containing the location details
   * @returns {Promise<AxiosResponse<T>>}
  */
  editUserLocation: (profileId, location) => instance.put(`/profiles/${profileId}/location`, {
    location
})
};

export const apiActivity = {
  /**
   * Sends a request to create a new activity
   */
  addActivity: (
    author_id,
    name,
    continuous,
    start_time,
    end_time,
    description,
    location,
    activity_types,
    visibility
  ) =>
    instance.post("/profiles/" + author_id + "/activities", {
      activity_name: name,
      continuous: continuous,
      start_time: start_time,
      end_time: end_time,
      description: description,
      location: location,
      activity_type: activitiesAddDashes(activity_types),
      visibility: visibility
    }),

  /**
   * Sends a request to edit an activity
   */
  editActivity: (
    author_id,
    name,
    continuous,
    start_time,
    end_time,
    description,
    location,
    activity_types,
    visibility,
    activity_id
  ) =>
    instance.put("/profiles/" + author_id + "/activities/" + activity_id, {
      activity_name: name,
      continuous: continuous,
      start_time: start_time,
      end_time: end_time,
      description: description,
      location: location,
      activity_type: activitiesAddDashes(activity_types),
      visibility: visibility,
      activity_id: activity_id,
    }),

  /**
   * Sends a request to retrieve an activity
   */
  getActivity: (activityId) => instance.get(`/activities/${activityId}`),

  /**
   * Sends a request to get an activity's updates
   */
  getActivityUpdates: (activityId, page, size) => instance.get(`/activities/${activityId}/changes/`, {
      params: {
          page: page,
          size: size
      }
  }),

  /**
   * Sends a request to add an achievement to an activity
   */
  addActivityAchievement: (profileId, activityId, name, description, resultType) =>
      instance.post(`/profiles/${profileId}/activities/${activityId}/achievements`,{
      name: name,
      description: description,
      resultType: resultType
  }),

  /**
   * Sends a request to edit an achievement
   */
  editActivityAchievement: (profileId, activityId, achievementId, name, description, resultType) =>
      instance.put(`/profiles/${profileId}/activities/${activityId}/achievements/${achievementId}`, {
      name: name,
      description: description,
      resultType: resultType
  }),

  /**
   * Sends a request to edit an activity's visibility
   */
  updateVisibilityAndGroups: (profileId, activityId, visibility, keepFollowers, keepParticipants, keepOrganisers) =>
    instance.put(`/profiles/${profileId}/activities/${activityId}/visibilityGroups`, {
      visibility: visibility,
      followers: keepFollowers,
      participants: keepParticipants,
      organisers: keepOrganisers,
    }
  ),

  /**
   * Send a request to delete an achievement
   */
  deleteActivityAchievement: (profileId, activityId, achievementId) =>
    instance.delete(`/profiles/${profileId}/activities/${activityId}/achievements/${achievementId}`),

  /**
   * Sends a request to retrieve an achievement
   */
  getActivityAchievement: (profileId, activityId) =>
    instance.get(`/profiles/${profileId}/activities/${activityId}/achievements`),

  /**
   * Sends a request to delete an activity
   */
  deleteActivity: (authorId, activityId) =>
    instance.delete(`/profiles/${authorId}/activities/${activityId}`),

  /**
   * Sends a request to get an activity's participants
   */
  getParticipants: (activityId, page, size) =>
    instance.get(`/activities/${activityId}/participants/`, {
      params: {
        page: page,
        size: size
      }
    }),

  /**
   * Sends a request to get an activity's organisers
   */
  getOrganisers: (activityId, page, size) =>
    instance.get(`/activities/${activityId}/organizers/`, {
      params: {
        page: page,
        size: size
      }
    }),

  /**
   * Send a request to get an activity from its id
   */
  async getActivityById(activityId) {
    return await apiActivity.getActivity(activityId).then(
        (response) => {
          for (let i = 0; i < response.data.activity_type.length; i++) {
            response.data.activity_type[i].name = response.data.activity_type[i].name.replace(/-/g, " ")
          }
          return response.data;
        },
        (error) => {
          if (error) {
            return "Invalid permissions";
          }
        }
    );
  },

  /***
   * Makes a request to set the number of people that can view a restricted activity.
   * @param emails: the emails of the users that can view the activity.
   * @param role: the role the users have in that activity.
   * @param profileId: the id of the creator of the activity.
   * @param activityId: the activity.
   * @returns a status code saying if the update was successful or was rejected for some reason.
   */
  async setActivityMembers(emails, role, profileId, activityId) {
    let membersList = [];
    for (let email of emails) {
      let userDetails = {"email": email, "role": role};
      membersList.push(userDetails);
    }
      return await instance.put(`/profiles/${profileId}/activities/${activityId}/visibility`, {
            "visibility": "restricted",
            "accessors": membersList
        }
    );
  },

  /**
   * Send a request to follow an activity
   */
  followActivity: (profileId, activityId) =>
    instance.post(`/profiles/${profileId}/subscriptions/activities/${activityId}`),

  /**
   * Send a request to unfollow an activity
   */
  unfollowActivity: (profileId, activityId) =>
      instance.delete(`/profiles/${profileId}/subscriptions/activities/${activityId}`),

  /**
   * Sends a request to add a result
   * @param profileId Id of profile
   * @param achievementId Id of achievement to add result to
   * @param resultValue value of result
   */
  addResult: (profileId, achievementId, resultValue) =>
      instance.post(`/profiles/${profileId}/achievements/${achievementId}/results`, {
        value: resultValue,
      }),

  /**
   * Sends a get request to retrieve one result
   * @param profileId Id of profile
   * @param achievementId Id of achievement
   * @param resultId Id of result
   */
  getOneResult: (profileId, achievementId, resultId) =>
      instance.get(`/profiles/${profileId}/achievements/${achievementId}/results/${resultId}`),

  /**
   * Sends a get request to retrieve all results
   * @param achievementId
   */
  getResults: (achievementId) =>
    instance.get(`/activities/achievements/${achievementId}/results`),

  /**
   * Send a request to retrieve the shared users for an activity
   */
  getSharedUsers: (activityId, currentPage, size) => instance.get(`/activities/${activityId}/shared`,{
      params: {
          page: currentPage,
          size: size
      }
  }),

  /**
   * Sends a request to edit a users role within an activity
   */
  editUserActivityRole: (profileId, activityId, role, email) =>
    instance.put(`/profiles/${profileId}/activities/${activityId}/subscriber`, {
      "subscriber": {
        "email": email,
        "role": role
      }
  }),

  /**
   * Send a request to search for an activity
   */
    getSearchedActivity: (searchTerm, searchTerms, method, currentPage, size) => instance.get(`/activities`,{
    params: {
        activitySearchTerm: searchTerm,
        activitySearchTerms: searchTerms.join(" "),
        method: method,
        page: currentPage,
        size: size
      }
    }),

  /**
   * Send a request to remove role in an activity
   */
  optOutOfActivityRole: (activityId, userEmail) => instance.delete(`/activities/${activityId}/roles/${userEmail}`),

  /**
   * Get the stats for an activity
   */
  getActivityStats: (activityId) => instance.get(`/activities/${activityId}/stats`),

  /**
   * Send a request to get the role of a user within an activity
   */
  getUserRole: (activityId, userId) => instance.get(`/activities/${activityId}/role/${userId}`),

  /**
   * Check the visibility of an activity for a user
   */
  checkUserActivityVisibility: (profileId, activityId) => instance.get(`/activities/${activityId}/profiles/${profileId}/uservisibility`),

    /**
     * Get location for activity with provided id
      */
    getLocationForActivity: (activityId) => instance.get(`/activities/${activityId}/location`),
  /**
  * Send a request to retrieve the all the activities within the given bound
  */
  getActivityInRange: (latitudeBottomLeft, latitudeTopRight, longitudeBottomLeft, longitudeTopRight) => instance.get(`/activities/within/range`,{
    params: {
        latitudeBottomLeft: latitudeBottomLeft,
        latitudeTopRight: latitudeTopRight,
        longitudeBottomLeft: longitudeBottomLeft,
        longitudeTopRight: longitudeTopRight
    }
}),
};
