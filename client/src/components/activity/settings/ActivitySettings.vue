<template>
  <div @click="showLocations = false">
<!--    <v-snackbar-->
<!--      v-model="snackbar"-->
<!--    >-->
<!--      {{ snackbarText }}-->
<!--      <v-btn-->
<!--        @click="snackbar = false"-->
<!--        color="primary"-->
<!--        text-->
<!--      >-->
<!--        Close-->
<!--      </v-btn>-->
<!--    </v-snackbar>-->
    <v-alert type="error" v-model="alertComponent" :timeout="timeout" dismissible prominent>
      {{errorMessage}}
    </v-alert>
    <v-overlay :value="overlayLoader" style="z-index: 1000">
      <v-progress-circular indeterminate size="64"></v-progress-circular>
    </v-overlay>
    <div class="createActivityContainer">
      <div class="createActivityContentContainer">
        <h1 id="createHeading">Create Activity</h1>
        <h1 id="createSubheading">Create your own activity.</h1>
        <form class="CreateActivityFormContainer">
          <v-card style="margin-top:20px;border-radius:15px;">
            <v-tabs grow show-arrows v-model="tabs">
              <v-tab id="BasicInfoTab">
                Basic Info*
              </v-tab>
              <v-tab id="TimeDateTab">
                Time/Date*
              </v-tab>
              <v-tab id="LocationTab">
                Location
              </v-tab>
              <v-tab id="AchievementTab">
                Achievements
              </v-tab>
            </v-tabs>

            <v-tabs-items v-model="tabs">
              <v-tab-item>
                <v-card class="py-3" flat>
                  <label class="editActivityLabel" for="name" id="ActivityNameLabel">Activity Name*</label>
                  <input class="editActivityInput" id="name" placeholder="Activity Name" required type="text"
                         v-model="name"/>
                  <label class="editActivityLabel" id="VisibilityLabel">Visibility</label>
                  <v-row no-gutters style="margin:0 20px;">
                    <v-radio-group row v-model="visibility">
                      <v-radio color="green"
                               id="publicVisibility"
                               label="Public"
                               value="public"
                      />
                      <v-radio color="orange"
                               id="restrictedVisibility"
                               label="Restricted"
                               value="restricted"
                      />
                      <v-radio color="red"
                               id="privateVisibility"
                               label="Private"
                               value="private"
                      />
                    </v-radio-group>
                  </v-row>

                  <label class="editActivityLabel" for="desc" id="DescriptionLabel">Description</label>
                  <textarea
                    class="editActivityTextarea"
                    id="desc"
                    maxlength="255"
                    placeholder="Activity Description"
                    type="text"
                    v-model="description">
                  </textarea>
                  <label class="editActivityLabel" id="ActivityTypeLabel" style="">Activity Types*</label>
                  <v-select
                    :items="activities_option"
                    attach
                    chips
                    id="ActivityTypeOptions"
                    label="Select Activity Types"
                    multiple
                    outlined
                    rounded
                    style="margin:0 20px;margin-top:5px;"
                    v-model="activity_types_selected"
                  />
                </v-card>
              </v-tab-item>
              <v-tab-item>
                <v-card class="py-3" flat>
                  <label class="editActivityLabel">Continuous?</label>
                  <v-row no-gutters style="margin:0 20px;">
                    <v-radio-group row v-model="duration" v-on:change="setDuration">
                      <v-radio
                        color="green"
                        id="continuousButton"
                        label="Continuous"
                        value="continuous"

                      />
                      <v-radio
                        color="green"
                        id="durationButton"
                        label="Duration"
                        value="duration"
                      />
                    </v-radio-group>
                  </v-row>

                  <label class="editActivityLabel" for="start_date" id="startDateLabel">Start Date*</label>
                  <input class="editActivityInput" id="start_date" type="date" v-model="start_date"/>

                  <label class="editActivityLabel" for="end_date" id="endDateLabel">End Date*</label>
                  <input class="editActivityInput" id="end_date" type="date" v-model="end_date"/>

                  <label class="editActivityLabel" for="start_time" id="startTimeLabel">Start Time*</label>
                  <input class="editActivityInput" id="start_time" type="time" v-model="start_time"/>

                  <label class="editActivityLabel" for="end_time" id="endTimeLabel">End Time*</label>
                  <input class="editActivityInput" id="end_time" type="time" v-model="end_time"/>
                </v-card>
              </v-tab-item>
              <v-tab-item>
                <v-card class="py-3" flat>
                  <div>
                    <ActivityLocationSettings v-on:set-location="setLocationFromComponent"/>
                  </div>
                </v-card>
              </v-tab-item>
              <v-tab-item>
                <v-card flat>
                  <v-card-text style="text-align: center;color:grey;" v-if="achievements.length == 0">No Achievements
                    Yet
                  </v-card-text>
                  <v-row align="center" justify="center" no-gutters v-bind:key="index"
                         v-for="(achievement, index) in achievements">
                    <v-card style="width:100%;padding:20px;margin:15px;border-radius: 15px;">
                      <v-card-text style="padding: 0;word-break: break-word;">{{achievement.resultType}}</v-card-text>
                      <v-card-title style="padding: 0;word-break: break-word;">{{achievement.name}}</v-card-title>
                      <v-card-text style="padding: 0;word-break: break-word;">{{achievement.description}}</v-card-text>
                      <v-spacer></v-spacer>
                      <v-menu bottom left>
                        <template v-slot:activator="{ on, attrs }">
                          <v-btn
                            rounded
                            style="position:absolute;width:36px;min-width:36px;right:15px;top:15px;"
                            text
                            v-bind="attrs"
                            v-on="on"
                          >
                            <v-icon>mdi-dots-vertical</v-icon>
                          </v-btn>
                        </template>

                        <v-list>
                          <v-list-item @click="setTempAchievement(achievement)">
                            <v-list-item-title>Edit</v-list-item-title>
                          </v-list-item>
                          <v-list-item @click="deleteAchievement(achievement)">
                            <v-list-item-title>Remove</v-list-item-title>
                          </v-list-item>
                        </v-list>
                      </v-menu>
                    </v-card>
                  </v-row>

                  <v-dialog v-model="editDialog" width="33%">
                    <v-card>
                      <v-card-title class="headline">Edit Achievement</v-card-title>

                      <v-row justify="center" no-gutters>
                        <v-card color="#3bb18b" style="padding:10px;border-radius:15px;width:100%;margin: 15px;">
                          <v-row no-gutters style="margin-top: 10px;">
                            <v-col>
                              <v-text-field
                                color="white" dark
                                dense id="achievementTitle" label="Achievement Title" outlined
                                placeholder="Achievement title" required rounded
                                style="margin-right: 5px"
                                v-model="tempTitle"/>
                            </v-col>
                            <v-col cols="5.5">
                              <v-select
                                :items="options"
                                color="white"
                                dark
                                dense
                                id="achieveType"
                                label="Select achievement type"
                                outlined
                                rounded
                                style="margin-left: 5px;"
                                v-model="tempResultType"
                              />
                            </v-col>
                          </v-row>
                          <v-row no-gutters>
                            <v-col>
                              <v-textarea
                                dark
                                densecolor="white"
                                id="achievementDescription"
                                label="Achievement Description"
                                maxlength="255"
                                outlined
                                placeholder="Achievement Description"
                                row-height="30"
                                rows="2"
                                type="text"
                                v-model="tempDescription">
                              </v-textarea>
                            </v-col>
                          </v-row>
                          <v-row no-gutters>
                            <v-spacer></v-spacer>
                            <v-btn :disabled="overlayLoader"
                                   @click="editDialog = false"
                                   color="#f06a6a"
                                   dark
                                   right
                                   rounded
                                   style="background-color:white;margin-left: 10px" text>Cancel
                            </v-btn>
                            <v-btn :disabled="overlayLoader"
                                   @click="saveEditedAchievement(tempTitle, tempDescription, tempResultType)"
                                   color="#3bb18b"
                                   dark
                                   right
                                   rounded
                                   style="background-color:white;margin-left: 10px"
                                   text>Save
                            </v-btn>
                          </v-row>
                        </v-card>
                      </v-row>
                    </v-card>
                  </v-dialog>
                  <v-divider></v-divider>
                  <v-row id="addAchievementBox" justify="center" no-gutters v-if="addAchievement">
                    <v-card color="#3bb18b"
                            style="padding:10px;padding-top:15px;border-radius:15px;width:100%;margin: 15px;">
                      <form>
                        <v-row no-gutters style="margin-bottom:10px;">
                          <v-col>
                            <v-text-field
                              color="white"
                              dark
                              dense
                              label="Achievement Title"
                              outlined
                              required
                              rounded
                              style="margin-right: 5px;"
                              v-model="achieveTitle"
                            />
                          </v-col>
                          <v-col>
                            <v-select
                              :items="options"
                              color="white"
                              dark
                              dense
                              id="achieveType"
                              label="Select achievement type"
                              outlined
                              rounded
                              style="margin-left: 5px;"
                              v-model="achieveType"
                            />
                          </v-col>
                        </v-row>
                        <v-row no-gutters style="margin-bottom:5px;">
                          <v-col>
                            <v-textarea
                              color="white"
                              dark
                              dense
                              label="Achievement Description"
                              outlined
                              placeholder="Enter Achievement Description"
                              rounded
                              row-height="30"
                              rows="2"
                              v-model="achieveDesc"
                            ></v-textarea>
                          </v-col>
                        </v-row>
                        <v-row no-gutters>
                          <v-spacer></v-spacer>
                          <v-btn
                            :disabled="overlayLoader"
                            color="white"
                            dark
                            id="cancelAchievementButton"
                            right
                            rounded
                            text
                            v-on:click="cancelAddAchievement()"
                          >
                            Cancel
                          </v-btn>
                          <v-btn
                            :disabled="overlayLoader"
                            color="#3bb18b"
                            dark
                            id="addAchievementButton"
                            right
                            rounded
                            style="background-color:white;margin-left: 10px"
                            text
                            v-on:click="addNewAchievement(achieveTitle, achieveDesc, achieveType)"
                          >
                            Add
                          </v-btn>
                        </v-row>
                      </form>
                    </v-card>
                  </v-row>
                  <div class="text-center" style="padding-bottom:15px;" v-if="!addAchievement">
                    <v-btn class="mx-2" color="primary" dark fab outlined style="margin-top:25px;"
                           v-on:click="addAchievement = true">
                      <v-icon dark>mdi-plus</v-icon>
                    </v-btn>
                    <v-card-text id="addAchievementLabel" style="font-weight: 400;color:#1dca92;font-size: 16px">Add
                      Achievement
                    </v-card-text>
                  </div>
                </v-card>
              </v-tab-item>
            </v-tabs-items>
            <v-divider></v-divider>
            <v-row no-gutters>
              <v-btn :disabled="tabs <= 0" color="black" id="BackArrow" rounded style="margin: 15px;padding:0 10px;" text
                     v-on:click="tabs -= 1">
                <v-icon style="padding-right:5px;">mdi-arrow-left</v-icon>
                Back
              </v-btn>
              <v-spacer></v-spacer>
              <v-btn color="black" id="NextArrow" rounded style="margin: 15px;padding:0 10px;" text v-if="tabs < 3"
                     v-on:click="tabs += 1">
                Next
                <v-icon style="padding-left:5px;">mdi-arrow-right</v-icon>
              </v-btn>
              <v-btn
                :disabled="overlayLoader"
                color="primary"
                id="CreateActivityButton"
                outlined
                right
                rounded
                style="margin:15px 20px;"
                v-if="tabs >= 3"
                v-on:click="addActivity"
              >
                Create Activity
              </v-btn>
            </v-row>
          </v-card>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
  import {mapActions, mapGetters} from "vuex";
  import {apiActivity, apiUser} from "../../../api";
  import router from "../../../router";
  import ActivityLocationSettings from "./ActivityLocationSettings";
  //import googleMapsApi from "@/util/googleMapsApi";

  export default {
    name: "ActivitySettingsPage",
    components: {
      ActivityLocationSettings
    },
    data() {
      return {
        tabs: null,
        selected_activity: "Activity Type",
        activities_option: [],
        duration: "duration",
        name: "",
        description: "",
        activity_types_selected: [],
        start_date: null,
        end_date: null,
        start_time: null,
        end_time: null,
        combinedStartTime: null,
        combinedEndTime: null,
        suggestedLocations: [],
        showLocations: false,
        visibility: "public",
        dialog: false,
        search: null,
        searchState: null,
        isLoading: false,
        stateLoading: false,
        features: [],
        snackbar: false,
        snackbarText: "",
        overlayLoader: false,
        createdId: null,
        options: ["Word", "Quantity", "Time", "Money"],
        addAchievement: false,
        achieveTitle: "",
        achieveDesc: "",
        achieveType: "",
        editDialog: false,
        achievements: [],
        tempAchievement: null,
        index: null,
        tempTitle: null,
        tempDescription: null,
        tempResultType: null,
        autocomplete: null,
        location: null,
        alertComponent: false,
        errorMessage: null,
      };
    },

    computed: {
      ...mapGetters(["user"]),
    },

    created() {
      this.loadActivityTypes()
    },

    mounted() {
      this.checkLogin()
    },

    methods: {
      ...mapActions(["createActivity", "updateUserContinuousActivities", "getDataFromUrl", "updateUserDurationActivities", "addActivityAchievement"]),
      /** checks login and if not created redirects you to profile page **/
      checkLogin() {
        if (!this.$route || !this.$router) {
          return;
        }
        if (parseInt(this.$route.params.profileId, 10) !== this.user.profile_id && this.user.permission_level === 0) {
          this.$router.push('/profile');
        }
      },

      /** Ensures only activity types from the database can be selected and cannot select ones already selected **/
      loadActivityTypes() {
        apiUser
          .getActivityTypes()
          .then(response => {
            this.activities_option = response.data;
            for (let i = 0; i < this.activities_option.length; i++) {
              this.activities_option[i] = this.activities_option[i].replace(/-/g, " ")
            }
          })
          .catch(error => console.log(error));
      },

      /**
       * Sets location from edit location component
       */
      setLocationFromComponent(newLocation) {
        this.location = newLocation;
      },

      /**
       * Resets achievement input
       */
      cancelAddAchievement() {
        this.addAchievement = false;
        this.achieveTitle = "";
        this.achieveDesc = "";
        this.achieveType = "";
      },

      /**
       * The function adds the achievement to the list of achievements.
       * */
      addNewAchievement(title, description, type) {
        if (title === null || title.trim() === "") {
          this.displayError("Please enter a title.");
        } else if (this.type === null || type.trim() === "") {
          this.displayError("Please enter an achievement type.");
        } else {
          this.achievements.push({'name': title, 'description': description, 'resultType': type});
          this.cancelAddAchievement();
        }
      },

      /**
       * The function over writes the saved achievement if the user decides to edit it before saving the activity.
       * */
      saveEditedAchievement(title, description, type) {
        this.editDialog = false;
        this.achievements[this.index] = {'name': title, 'description': description, 'resultType': type};
        this.tempAchievement = null;
        this.index = null;
        this.tempTitle = null;
        this.tempDescription = null;
        this.tempResultType = null;
      },

      /**
       * The function deletes a specific achievements from the list of achievement.
       * */
      deleteAchievement(achievement) {
        if (this.achievements.length === 1) {
          this.achievements = [];
        } else {
          const index = this.achievements.indexOf(achievement);
          if (index > -1) {
            this.achievements.splice(index, 1);
          }
        }
      },

      /**
       * Assigns the temp achievement to the selected achievement form the list of achievements
       **/
      async setTempAchievement(achievement) {
        this.index = this.achievements.indexOf(achievement);
        this.tempAchievement = this.achievements[this.index]
        this.tempTitle = achievement.name
        this.tempDescription = achievement.description
        this.tempResultType = achievement.resultType
        this.editDialog = true
      },

      /**
       * Shows/hides date and time selection if duration is duration/continuous
       */
      setDuration() {
        if (this.duration === "duration") {
          document.getElementById("start_date").type = "date";
          document.getElementById("end_date").type = "date";
          document.getElementById("start_time").type = "time";
          document.getElementById("end_time").type = "time";
          document.getElementById("startDateLabel").hidden = false;
          document.getElementById("endDateLabel").hidden = false;
          document.getElementById("startTimeLabel").hidden = false;
          document.getElementById("endTimeLabel").hidden = false;
        } else {
          document.getElementById("start_date").type = "hidden";
          document.getElementById("end_date").type = "hidden";
          document.getElementById("start_time").type = "hidden";
          document.getElementById("end_time").type = "hidden";
          document.getElementById("startDateLabel").hidden = true;
          document.getElementById("endDateLabel").hidden = true;
          document.getElementById("startTimeLabel").hidden = true;
          document.getElementById("endTimeLabel").hidden = true;
        }
      },

      /**
       * Adds activity type to selected options
       */
      selectActivityType() {
        if (this.selected_activity !== undefined) {
          this.activity_types_selected.push(this.selected_activity);
          let index = this.activities_option.indexOf(this.selected_activity);
          if (index !== -1) {
            this.activities_option.splice(index, 1);
          }
        }
      },
      /**
       * Removes activity type from selection
       */
      removeActivityType(addedActivity) {
        this.activities_option.push(addedActivity);
        let index = this.activity_types_selected.indexOf(addedActivity);
        if (index !== -1) {
          this.activity_types_selected.splice(index, 1);
        }
      },
      /**
       * Combines the times and dates given in the form to a single datetime format
       * Sets datetime to null if continuous activity
       */
      combineDateTime() {
        let currentDate = new Date(Date.now());
        let timeZone = currentDate.toString().slice(currentDate.toString().indexOf("+"), 5);

        if (this.duration !== "duration") {
          this.combinedStartTime = null;
          this.combinedEndTime = null;
        } else if (this.start_time === null && this.end_time === null) {
          this.combinedStartTime = this.start_date + "T00:00:00" + timeZone;
          this.combinedEndTime = this.end_date + "T00:00:00" + timeZone;
        } else if (this.start_time === null) {
          this.combinedStartTime = this.start_date + "T00:00:00" + timeZone;
          this.combinedEndTime =
            this.end_date + "T" + this.end_time + ":00" + timeZone;
        } else if (this.end_time === null) {
          this.combinedStartTime = this.start_date + "T" + this.start_time + ":00" + timeZone;
          this.combinedEndTime = this.end_date + "T00:00:00" + timeZone;
        } else {
          this.combinedStartTime = this.start_date + "T" + this.start_time + ":00" + timeZone;
          this.combinedEndTime = this.end_date + "T" + this.end_time + ":00" + timeZone;
        }
      },
      /**
       * Checks if the datetime passes conditions. Ensures time is in the future and start is not later than end
       * @return boolean true if passes, false if fails
       */
      checkTimeContinuity() {
        let currentDate = new Date(Date.now());
        let timeZone = currentDate.toString().slice(currentDate.toString().indexOf("+"), 5);

        currentDate.setHours(0);
        currentDate.setMinutes(0);
        currentDate.setSeconds(0);
        currentDate.setMilliseconds(0);
        if (new Date(this.combinedStartTime) > new Date(this.combinedEndTime)) {
          this.displayError("End time must be after start time.");
          return false;
        } else if (currentDate > new Date(this.start_date + "T00:00:00" + timeZone)) {
          this.displayError("Start date must be in the future.");
          return false;
        } else if (currentDate.getFullYear() + 2 < new Date(this.start_date + "T00:00:00" + timeZone).getFullYear()
          || currentDate.getFullYear() + 2 < new Date(this.end_date + "T00:00:00" + timeZone).getFullYear()) {
          this.displayError("Must be less than 2 years in the future.");
          return false;
        }
        return true;
      },
      /**
       * Navigates the user to the Edit Achievement component page if they would like
       * to add achievements to the their activities.
       */
      navigateToCreateAchievement() {
        this.setAchievement = true;
        this.addActivity();
        if (this.setAchievement === true) {
          this.$router.push({path: '/activity/achievement_setting/' + this.createdId});
        }
      },

      /**
       * Check all activity form conditions
       * @return boolean true if passes, false if fails
       */
      checkFormConditions() {
        if (this.name === null || this.name.trim() === "") {
          // Name is empty
          this.displayError("Please select an activity name.");

          this.tabs = 0;
          return false;
        } else if (this.activity_types_selected.length < 1) {
          // No activity types selected
          this.displayError("Please select at least one activity type.");
          this.tabs = 0;
          return false;
        } else if (this.duration !== "duration" && this.duration !== "continuous") {
          // Duration is not set
          this.displayError("Please select a duration.");
          this.tabs = 1;
          return false;
        } else if (this.duration === "duration" &&
          (this.start_date === null || this.end_date === null || this.start_date === "" || this.end_date === "")) {
          // Start or end date not set
          this.displayError("Please select start and end date.");
          this.tabs = 1;
          return false;
        } else if (this.duration === "duration" && !this.checkTimeContinuity()) {
          // Time check failed
          this.tabs = 1;
          return false;
        } else {
          // All passed
          return true;
        }
      },

      /**
       * Checks form conditions and sends create activity request if conditions pass
       */
      addActivity() {
        this.overlayLoader = true;
        // Combines dates and times, must be done before checking form
        this.combineDateTime();

        // Checks all activity attribute conditions
        if (!this.checkFormConditions()) {
          this.overlayLoader = false;
          return;
        }

        // Sets duration to a boolean for the request
        var tempIsDuration = this.duration !== "duration";

        // Send a create request
        apiActivity.addActivity(this.$route.params.profileId, this.name, tempIsDuration, this.combinedStartTime,
          this.combinedEndTime, this.description, this.location, this.activity_types_selected, this.visibility)
          .then(
            response => {
              if (response) {
                this.createdId = response.data;
                apiUser.getUserContinuousActivities(this.user.profile_id)
                  .then(response => {
                    this.updateUserContinuousActivities(response.data);
                  });
                apiUser.getUserDurationActivities(this.user.profile_id)
                  .then(response => {
                    this.updateUserDurationActivities(response.data);
                  });
                if (this.achievements.length > 0) {
                  for (let i = 0; i < this.achievements.length; i++) {
                    apiActivity.addActivityAchievement(this.user.profile_id, this.createdId, this.achievements[i].name,
                      this.achievements[i].description, this.achievements[i].resultType.toUpperCase())
                  }

                }
                router.push("/activity/" + this.createdId);
              }
            },
            error => {
              this.overlayLoader = true;
              if (error) {
                this.displayError(error)
              }
            });
      },
      /**
       * Shows error text for given error string
       * @param error
       */
      displayError(error) {
        this.alertComponent = true;
        this.errorMessage = error;
      },
    },
  };
</script>

<style scoped>
  @import "../../../../public/styles/pages/activitySettingsStyle.css";

</style>
