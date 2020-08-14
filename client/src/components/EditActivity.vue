<template>
  <div @click="showLocations = false">
    <v-snackbar
            v-model="snackbar"
    >
      {{ snackbarText }}
      <v-btn
              color="primary"
              text
              @click="snackbar = false"
      >
        Close
      </v-btn>
    </v-snackbar>
    <v-overlay style="z-index: 1000" :value="overlayLoader">
      <v-progress-circular indeterminate size="64"></v-progress-circular>
    </v-overlay>
    <div class="createActivityContainer">
      <div class="createActivityContentContainer">
        <h1 id="createHeading">Edit Activity</h1>
        <h1 id="createSubheading">Make changes to your activity.</h1>
        <form class="CreateActivityFormContainer">
          <v-card style="margin-top:20px;border-radius:15px;" :loading="pageLoading" :disabled="pageLoading">
            <v-tabs v-model="tabs" grow show-arrows>
              <v-tab>
                Basic Info*
              </v-tab>
              <v-tab>
                Time/Date*
              </v-tab>
              <v-tab>
                Location
              </v-tab>
              <v-tab>
                Achievements
              </v-tab>
            </v-tabs>

            <v-tabs-items v-model="tabs">
              <v-tab-item>
                <v-card flat class="py-3">
                  <label class="editActivityLabel" for="name">Activity Name*</label>
                  <input class="editActivityInput" type="text" id="name" v-model="activity_name" placeholder="Activity Name" required />
                  <label class="editActivityLabel">Visibility</label>
                  <v-row no-gutters style="margin:0 20px;">
                    <v-radio-group v-model="visibility" row>
                      <v-radio
                              label="Public"
                              color="green"
                              value="public"
                      ></v-radio>
                      <v-radio
                              label="Restricted"
                              color="orange"
                              value="restricted"
                      ></v-radio>
                      <v-radio
                              label="Private"
                              color="red"
                              value="private"
                      ></v-radio>
                    </v-radio-group>
                  </v-row>

                  <label class="editActivityLabel" for="desc">Description</label>
                  <textarea
                          class="editActivityTextarea"
                          maxlength="255"
                          type="text"
                          id="desc"
                          v-model="description"
                          placeholder="Activity Description">
                  </textarea>
                  <label class="editActivityLabel">Activity Types*</label>
                  <v-select style="margin:0 20px;margin-top:5px;"
                          v-model="activity_types_selected"
                          :items="activities_option"
                          attach
                          chips
                          label="Select Activity Types"
                          multiple
                          rounded
                          outlined
                          :loading="activityTypesLoading"
                          :disabled="activityTypesLoading"
                  ></v-select>
                </v-card>
              </v-tab-item>
              <v-tab-item>
                <v-card flat class="py-3">
                  <label class="editActivityLabel">Continuous?</label>
                  <v-row no-gutters style="margin:0 20px;">
                    <v-radio-group v-model="duration" v-on:change="setDuration" row>
                      <v-radio
                              label="Continuous"
                              color="green"
                              value="continuous"
                      ></v-radio>
                      <v-radio
                              label="Duration"
                              color="green"
                              value="duration"
                      ></v-radio>
                    </v-radio-group>
                  </v-row>
                  <div v-if="isDuration">
                    <label class="editActivityLabel" id="startDateLabel" for="start_date">Start Date*</label>
                    <input class="editActivityInput" type="date" id="start_date" v-model="start_date" />

                    <label class="editActivityLabel" id="endDateLabel" for="end_date">End Date*</label>
                    <input class="editActivityInput" type="date" id="end_date" v-model="end_date" />

                    <label class="editActivityLabel" id="startTimeLabel" for="start_time">Start Time*</label>
                    <input class="editActivityInput" type="time" id="start_time" v-model="start_time" />

                    <label class="editActivityLabel" id="endTimeLabel" for="end_time">End Time*</label>
                    <input class="editActivityInput" type="time" id="end_time" v-model="end_time" />
                  </div>
                </v-card>
              </v-tab-item>
              <v-tab-item>
                <v-card flat class="py-3">
                  <div>
                    <v-combobox
                            v-model="city"
                            :items="items"
                            :search-input.sync="search"
                            color="primary"
                            :loading="isLoading"
                            no-filter
                            hide-no-data
                            item-text="Description"
                            item-value="API"
                            label="City"
                            placeholder="Start typing to Search"
                            return-object
                            id="inputCity"
                            outlined
                            class="locationCombo"
                            autocomplete="new"
                            dense
                            style="margin: 0 20px;"
                    />
                    <v-combobox
                            v-model="state"
                            :items="itemsState"
                            :search-input.sync="searchState"
                            color="primary"
                            no-filter
                            :loading="stateLoading"
                            hide-no-data
                            hide-selected
                            item-text="Description"
                            item-value="API"
                            label="State"
                            placeholder="Start typing to Search"
                            return-object
                            id="inputState"
                            outlined
                            class="locationCombo"
                            autocomplete="new"
                            dense
                            style="margin: 0 20px;"
                    />
                    <v-combobox
                            v-model="country"
                            :items="countries_option"
                            color="primary"
                            hide-no-data
                            hide-selected
                            item-text="Description"
                            label="Country"
                            placeholder="Start typing to Search"
                            return-object
                            id="inputCountry"
                            outlined
                            class="locationCombo"
                            autocomplete="new"
                            dense
                            style="margin: 0 20px;"
                    />
                  </div>
                </v-card>
              </v-tab-item>
              <v-tab-item>
                <v-card flat>
                  <v-card-text style="text-align: center;color:grey;" v-if="achievements.length == 0">No Achievements Yet</v-card-text>
                  <v-row justify="center" align="center" no-gutters v-for="(achievement, index) in achievements" v-bind:key="index">
                    <v-card style="width:100%;padding:20px;margin:15px;border-radius: 15px;">
                      <v-card-text style="padding: 0;word-break: break-word;">{{achievement.resultType}}</v-card-text>
                      <v-card-title style="padding: 0;word-break: break-word;">{{achievement.name}}</v-card-title>
                      <v-card-text style="padding: 0;word-break: break-word;">{{achievement.description}}</v-card-text>
                      <v-spacer></v-spacer>
                      <v-menu bottom left>
                        <template v-slot:activator="{ on, attrs }">
                          <v-btn
                                  text
                                  rounded
                                  style="position:absolute;width:36px;min-width:36px;right:15px;top:15px;"
                                  v-bind="attrs"
                                  v-on="on"
                          >
                            <v-icon>mdi-dots-vertical</v-icon>
                          </v-btn>
                        </template>

                        <v-list>
                          <v-list-item @click= "setTempAchievement(achievement)">
                            <v-list-item-title>Edit</v-list-item-title>
                          </v-list-item>
                          <v-list-item @click="openDeletePopUp(achievement)">
                            <v-list-item-title>
                              Remove
                            </v-list-item-title>
                          </v-list-item>
                        </v-list>
                      </v-menu>
                    </v-card>
                  </v-row>
                  <v-dialog width="33%" v-model="editDialog">
                    <v-card>
                      <v-card-title class="headline">Edit Achievement</v-card-title>

                      <v-row  justify="center" no-gutters>
                        <v-card style="padding:10px;border-radius:15px;width:100%;margin: 15px;" color="#3bb18b">
                          <v-row no-gutters style="margin-top: 10px;">
                            <v-col>
                              <v-text-field
                                      v-model="tempTitle" label="Achievement Title"
                                      placeholder="Achievement title" rounded outlined dense
                                      required style="margin-right: 5px" color="white"
                                      id="achievementTitle"
                                      dark/>
                            </v-col>
                            <v-col cols="5.5">
                              <v-select
                                      id = "achieveType"
                                      style="margin-left: 5px;"
                                      v-model="tempResultType"
                                      :items="options"
                                      label="Select achievement type"
                                      rounded
                                      outlined
                                      dense
                                      color="white"
                                      dark
                              />
                            </v-col>
                          </v-row>
                          <v-row no-gutters>
                            <v-col>
                              <v-textarea
                                      label="Achievement Description"
                                      maxlength="255"
                                      type="text"
                                      v-model="tempDescription"
                                      rows="2"
                                      row-height="30"
                                      outlined
                                      id="achievementDescription"
                                      densecolor="white"
                                      dark
                                      placeholder="Achievement Description">
                              </v-textarea>
                            </v-col>
                          </v-row>
                          <v-row no-gutters>
                            <v-spacer></v-spacer>
                            <v-btn color="#f06a6a"
                                   style="background-color:white;margin-left: 10px"
                                   rounded
                                   text
                                   right
                                   dark
                                   :disabled="overlayLoader"  @click="editDialog = false">Cancel</v-btn>
                            <v-btn color="#3bb18b"
                                   style="background-color:white;margin-left: 10px"
                                   rounded
                                   text
                                   right
                                   dark
                                   :disabled="overlayLoader" @click="saveEditedAchievement(tempTitle, tempDescription, tempResultType)">Save</v-btn>
                          </v-row>
                        </v-card>
                      </v-row>
                    </v-card>
                  </v-dialog>
                  <v-divider></v-divider>
                  <v-row justify="center" no-gutters v-if="addAchievement" id="addAchievementBox">
                    <v-card style="padding:10px;padding-top:15px;border-radius:15px;width:100%;margin: 15px;" color="#3bb18b">
                      <form>
                        <v-row no-gutters style="margin-bottom:10px;">
                          <v-col>
                            <v-text-field
                                    style="margin-right: 5px;"
                                    v-model="achieveTitle"
                                    label="Achievement Title"
                                    rounded
                                    required
                                    outlined
                                    dense
                                    color="white"
                                    dark
                            ></v-text-field>
                          </v-col>
                          <v-col>
                            <v-select
                                    style="margin-left: 5px;"
                                    id = "achieveType"
                                    v-model="achieveType"
                                    :items="options"
                                    label="Select achievement type"
                                    rounded
                                    outlined
                                    dense
                                    color="white"
                                    dark
                            />
                          </v-col>
                        </v-row>
                        <v-row no-gutters style="margin-bottom:5px;">
                          <v-col>
                            <v-textarea
                                    v-model="achieveDesc"
                                    rounded
                                    label="Achievement Description"
                                    placeholder="Enter Achievement Description"
                                    rows="2"
                                    row-height="30"
                                    outlined
                                    dense
                                    color="white"
                                    dark
                            ></v-textarea>
                          </v-col>
                        </v-row>
                        <v-row no-gutters>
                          <v-spacer></v-spacer>
                          <v-btn
                                  v-on:click="cancelAddAchievement()"
                                  color="white"
                                  rounded
                                  text
                                  right
                                  dark
                                  :disabled="overlayLoader"
                          >
                            Cancel
                          </v-btn>
                          <v-btn
                                  v-on:click="addNewAchievement(achieveTitle, achieveDesc, achieveType)"
                                  color="#3bb18b"
                                  style="background-color:white;margin-left: 10px"
                                  rounded
                                  text
                                  right
                                  dark
                                  :disabled="overlayLoader"
                          >
                            Add
                          </v-btn>
                        </v-row>
                      </form>
                    </v-card>
                  </v-row>
                  <div class="text-center" style="padding-bottom:15px;" v-if="!addAchievement">
                    <v-btn style="margin-top:25px;" class="mx-2" fab dark outlined color="primary" v-on:click="addAchievement = true">
                      <v-icon dark>mdi-plus</v-icon>
                    </v-btn>
                    <v-card-text style="font-weight: 400;color:#1dca92;font-size: 16px">Add Achievement</v-card-text>
                  </div>
                </v-card>
              </v-tab-item>
            </v-tabs-items>

            <v-row justify="center">
              <v-dialog
                  v-model="deleteDialog"
                  max-width="290"
              >
                <v-card>
                  <v-card-title class="headline">Delete achievement</v-card-title>

                  <v-card-text>
                    Are you sure you want to delete this achievement?
                  </v-card-text>

                  <v-card-actions>
                    <v-spacer></v-spacer>

                    <v-btn
                        color="green darken-1"
                        text
                        @click="deleteDialog = false"
                    >
                      Disagree
                    </v-btn>

                    <v-btn
                        color="green darken-1"
                        text
                        @click="deleteAchievement(tempAchievement)"
                    >
                      Agree
                    </v-btn>
                  </v-card-actions>
                </v-card>
              </v-dialog>
            </v-row>


            <v-divider></v-divider>
            <v-row no-gutters>
              <v-btn
                      v-on:click="deleteActivity()"
                      style="margin:15px 20px;"
                      color="red"
                      rounded
                      outlined
                      right
                      :disabled="overlayLoader"
              >
                Delete Activity
              </v-btn>
              <v-spacer></v-spacer>
              <v-btn
                      v-on:click="saveEditedActivity"
                      style="margin:15px 20px;"
                      color="primary"
                      rounded
                      outlined
                      right
                      :disabled="overlayLoader"
              >
                Update Activity
              </v-btn>
            </v-row>
          </v-card>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { mapGetters, mapActions } from "vuex";
import { apiUser, apiActivity } from "../api";
import router from "../router";

const COUNTRIES_URL = 'https://restcountries.eu/rest/v2/all'

export default {
  data() {
    return {
      tabs: null,
      selected_activity: "Activity Type",
      activities_option: [],
      countries_option: [],
      location: null,
      duration: "duration",
      activity_types_selected: [],
      start_date: null,
      end_date: null,
      start_time: null,
      end_time: null,
      activity_name: "",
      combinedEndTime: null,
      combinedStartTime: null,
      continuous: false,
      description: "",
      author_id: null,
      suggestedLocations: [],
      showLocations: false,
      visibility: null,
      city: null,
      country: null,
      state: null,
      locationCity: null,
      locationState: null,
      search: null,
      searchState: null,
      isLoading: false,
      stateLoading: false,
      features: [],
      snackbar: false,
      snackbarText: "",
      overlayLoader: false,
      pageLoading: true,
      activityTypesLoading: true,
      activityAchievements: [],
      addAchievement: false,
      options: ["Word", "Quantity", "Time", "Money"],
      achieveTitle: "",
      achieveDesc: "",
      achieveType: "",
      achievements: [],
      editDialog: false,
      tempAchievement: null,
      tempTitle: null,
      tempDescription: null,
      tempResultType: null,
      deleteDialog: false,
      confirmDelete: false
    };
  },

  /**
   * On start-up, the mounted function calls the rest api countries and updates the select drop down with the list of
   * countries for the user to choose.
   */
  mounted: function() {
      if (!this.user.isLogin) {
          this.$router.push('/login');
      } else {
        this.loadCountries();
      }
  },

  computed: {
    ...mapGetters(["user"]),
    isDuration() {
      return this.duration == "duration";
    },
    items () {
      return this.features.map(entry => {
        const Description = this.getLocationCity(entry);
        return Object.assign({}, entry, { Description })
      })
    },
    itemsState () {
      return this.features.map(entry => {
        const Description = this.getLocationState(entry);
        return Object.assign({}, entry, { Description })
      })
    }
  },
  created: async function() {
    await this.loadActivity();
    // Ensures only activity types from the database can be selected and cannot select ones already selected
    await apiUser
      .getActivityTypes()
      .then(response => {
        this.activities_option = response.data;
        for (let i = 0; i < this.activities_option.length; i++) {
          this.activities_option[i] = this.activities_option[i].replace(/-/g, " ")
        }
        this.activityTypesLoading = false;
      })
      .catch(error => console.log(error));
  },
  methods: {
    ...mapActions(["createActivity", "updateUserContinuousActivities", "updateUserDurationActivities",
      "addActivityAchievement", "editActivityAchievement", "deleteActivityAchievement", "getDataFromUrl",
       "getActivityAchievement"]),

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
      this.cancelAddAchievement();
      apiActivity.addActivityAchievement(this.user.profile_id, this.$route.params.activityId, title, description, type.toUpperCase());
      this.achievements.push({'name': title, 'description': description, 'resultType': type});
      this.achieveDesc= "";
      this.achieveTitle = "";
      this.achieveType = "";
    },

    /**
     * The function over writes the saved achievement if the user decides to edit it before saving the activity.
     * */
    saveEditedAchievement(title, description, type){
      this.editDialog = false;
      if (title === null || title.trim() === "") {
        this.displayError("Please enter a title.");
      }
      else {
        this.achievements[this.index] = {'name': title, 'description': description, 'resultType': type};
        apiActivity.editActivityAchievement(this.user.profile_id, this.$route.params.activityId, this.tempAchievement.id, title, description, type.toUpperCase());
        apiActivity.getActivityAchievement(this.user.profile_id, this.$route.params.activityId);
        for (let i = 0; i < this.achievements.length; i++) {
          this.achievements[i].resultType = this.achievements[i].resultType.toLowerCase();
          this.achievements[i].resultType = this.achievements[i].resultType[0].toUpperCase() + this.achievements[i].resultType.slice(1);
        }
        this.loadActivity()
        this.tempAchievement = null;
        this.index = null;
        this.tempTitle = null;
        this.tempDescription = null;
        this.tempResultType = null;
      }

    },

    /**
     * Used to manage the delete pop up box when deleting an achievement
     * */
    openDeletePopUp(achievement){
      this.deleteDialog = true;
      this.tempAchievement = achievement;
    },
    /**
     * The function deletes a specific achievements from the list of achievement.
     * */
    deleteAchievement(achievement){
      apiActivity.deleteActivityAchievement(this.user.profile_id, this.$route.params.activityId, achievement.id);
      this.loadActivity();
      this.tempAchievement = null;
      this.deleteDialog = false;
    },

    /**
     * Assigns the temp achievement to the selected achievement form the list of achievements
     **/
    setTempAchievement(achievement){

      // check if the response from the endpoint has any results entered against this achievement
      //if any results are entered then open a pop up to let the user know they cannot delete /edit this anymore
      // make the call to the function that opens that pop up
      //else let the user edit the activity
      console.log(this.achievements);
      this.index = this.achievements.indexOf(achievement);
      this.tempAchievement = this.achievements[this.index];
      this.tempTitle = achievement.name;
      this.tempDescription = achievement.description;
      this.tempResultType = achievement.resultType.toLowerCase();
      this.tempResultType = this.tempResultType[0].toUpperCase() + this.tempResultType.slice(1);
      this.editDialog=true
    },

    /**
     * This method filters the the data received from the api and only suggests cities to the user.
     *
     */
    getLocationCity(location) {
      let city = "Almora";
      if(location.properties.city !== undefined){
        city = location.properties.city;
        return city;
      }
      return city;
    },

    /**
     * This method filters the the data received from the api and only suggests states to the user.
     *
     */
    getLocationState(location) {
      let state = "Angland";
      if(location.properties.state !== undefined){
        state = location.properties.state;
        return state
      }
      return state;
    },

    /**
     * Sets the location and resets the location input after the user has selected a location from the dropdown
     * box.
     */
    setLocation() {
      this.location = "";
      this.city ="";
      this.state="";
      this.country="";
      if (document.getElementById('inputCity') !== null) {
        this.city = document.getElementById('inputCity').value;
      }
      if (document.getElementById('inputState') !== null) {
        this.state = document.getElementById('inputState').value;
      }
      if (document.getElementById('inputCountry') !== null) {
        this.country = document.getElementById('inputCountry').value;
      }
      this.location = this.city + ',' + this.state + ',' + this.country
    },

    /*
      Uses activity id from url to request activity data.
    */
    async loadActivity() {
      if (
        this.$route.params.activityId == null ||
        this.$route.params.activityId == ""
      ) {
        this.$router.push("/profile");
      } else {
        var tempActivityData = await apiActivity.getActivityById(
          this.$route.params.activityId
        );
        if (tempActivityData == "Invalid permissions") {
          this.$router.push("/profile");
        } else {
          let tempAchievements = await apiActivity.getActivityAchievement(tempActivityData.author.profile_id,this.$route.params.activityId);
          this.achievements = tempAchievements.data;
          for (let i = 0; i < this.achievements.length; i++) {
            this.achievements[i].resultType = this.achievements[i].resultType.toLowerCase();
            this.achievements[i].resultType = this.achievements[i].resultType[0].toUpperCase() + this.achievements[i].resultType.slice(1);
          }
          this.pageLoading = false;
          this.activity_name = tempActivityData.activity_name;
          this.continuous = tempActivityData.continuous;
          if (this.continuous) {
            this.duration = "continuous";
          } else {
            this.duration = "duration";
            this.end_time = this.formatTime(tempActivityData.end_time);
            this.start_time = this.formatTime(tempActivityData.start_time);
            this.start_date = this.formatDate(tempActivityData.start_time);
            this.end_date = this.formatDate(tempActivityData.end_time);
          }
          this.author_id = tempActivityData.author.profile_id;
          this.description = tempActivityData.description;
          this.activity_type = tempActivityData.activity_type.slice();
          this.location = tempActivityData.location;
          this.visibility = tempActivityData.visibility;

          for (let i = 0; i < tempActivityData.activity_type.length; i++) {
            tempActivityData.activity_type[i].name = tempActivityData.activity_type[i].name.replace(/-/g, " ")
          }
          if(typeof this.location !== 'undefined' & this.location != null){
            let cityStateCountry = this.location.split(",");
            if(typeof cityStateCountry[0] !== 'undefined'){
              this.city = cityStateCountry[0];
            }
            if(typeof cityStateCountry[1] !== 'undefined'){
              this.state = cityStateCountry[1];
            }
            if(typeof cityStateCountry[2] !== 'undefined'){
              this.country = cityStateCountry[2];
            }
          }
          this.activity_types_selected = tempActivityData.activity_type.map(
            e => e.name
          );
        }
      }
    },

    /**
     * Shows/hides date and time selection if duration is duration/continuous
     */
    setDuration() {
      this.start_date = null;
      this.end_date = null;
      this.start_time = null;
      this.end_time = null;
    },
    /**
     * This function converts the milli seconds to the format YYYY-MM-DD
     */
    formatDate(date) {
      let d = new Date(date);
      let month = "" + (d.getMonth() + 1);
      let day = "" + d.getDate();
      let year = d.getFullYear();
      if (month.length < 2) month = "0" + month;
      if (day.length < 2) day = "0" + day;
      return [year, month, day].join("-");
    },

    formatTime(date) {
      let d = new Date(date);
      let hour = "" + d.getHours();
      let minutes = "" + d.getMinutes();
      if (hour.length < 2) hour = "0" + hour;
      if (minutes.length < 2) minutes = "0" + minutes;
      return [hour, minutes].join(":");
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
      let timeZone = currentDate
        .toString()
        .slice(currentDate.toString().indexOf("+"), 5);

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
        this.combinedStartTime =
          this.start_date + "T" + this.start_time + ":00" + timeZone;
        this.combinedEndTime = this.end_date + "T00:00:00" + timeZone;
      } else {
        this.combinedStartTime =
          this.start_date + "T" + this.start_time + ":00" + timeZone;
        this.combinedEndTime =
          this.end_date + "T" + this.end_time + ":00" + timeZone;
      }
    },

    /**
     * The method is used to populate the drop down menu, that allows user to select their current country.
     */
    loadCountries() {
      this.getDataFromUrl(COUNTRIES_URL)
          .then((response) => {
            const countries = [];
            const data = response.data;
            for (let country in data) {
              let country_name = data[country].name;
              countries.push(country_name)
            }
            this.countries_option = countries
          })
          .catch(error => console.log(error));
    },

    /**
     * Checks if the datetime passes conditions. Ensures time is in the future and start is not later than end
     * @return boolean true if passes, false if fails
     */
    checkTimeContinuity() {
      let currentDate = new Date(Date.now());
      let timeZone = currentDate
        .toString()
        .slice(currentDate.toString().indexOf("+"), 5);

      currentDate.setHours(0);
      currentDate.setMinutes(0);
      currentDate.setSeconds(0);
      currentDate.setMilliseconds(0);
      if (new Date(this.combinedStartTime) > new Date(this.combinedEndTime)) {
        this.displayError("End time must be after start time.");
        return false;
      } else if (
        currentDate > new Date(this.start_date + "T00:00:00" + timeZone)
      ) {
        this.displayError("Start date must be in the future.");
        return false;
      } else if (
        currentDate.getFullYear() + 2 <
          new Date(this.start_date + "T00:00:00" + timeZone).getFullYear() ||
        currentDate.getFullYear() + 2 <
          new Date(this.end_date + "T00:00:00" + timeZone).getFullYear()
      ) {
        this.displayError("Must be less than 2 years in the future.");
        return false;
      }
      return true;
    },

    /**
     * Check all activity form conditions
     * @return boolean true if passes, false if fails
     */
    checkFormConditions() {
      if (this.activity_name === null || this.activity_name.trim() === "") {
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
    saveEditedActivity() {
      // Combines dates and times, must be done before checking form
      this.combineDateTime();

      // Checks all activity attribute conditions
      if (!this.checkFormConditions()) {
        return;
      }
      this.overlayLoader = true;
      this.setLocation(location);

      // Sets duration to a boolean for the request
      this.duration = this.duration !== "duration";

      apiActivity
        .editActivity(
          this.user.profile_id,
          this.activity_name,
          this.duration,
          this.combinedStartTime,
          this.combinedEndTime,
          this.description,
          this.location,
          this.activity_types_selected,
          this.visibility,
          this.$route.params.activityId
        )
        .then(
          response => {
            if(response) {
              apiUser
                  .getUserContinuousActivities(this.user.profile_id)
                  .then(response => {
                    this.updateUserContinuousActivities(response.data);
                  });
              apiUser
                  .getUserDurationActivities(this.user.profile_id)
                  .then(response => {
                    this.updateUserDurationActivities(response.data);
                  });
              router.push("/activity/" + this.$route.params.activityId);
            }
          },
          error => {
              this.overlayLoader = true;
              if(error){
                this.displayError("An error occurred.");
              }
          }
        );
    },

    /**
     * The method is used to filter out the feature object without any cities
     */
    removeNullCities(features){
      let featuresCity = [];
      for(const feature of features){
        if(feature.properties.city !== undefined) {
          featuresCity.push(feature);
        }
      }
      return featuresCity;
    },

    /**
     * The method is used to filter out the feature object without any states
     */
    removeNullState(features){
      let featuresState = [];
      for(const feature of features){
        if(feature.properties.state !== undefined) {
          featuresState.push(feature);
        }
      }
      return featuresState;
    },

    deleteActivity() {
      apiActivity
        .deleteActivity(this.user.profile_id, this.$route.params.activityId)
        .then(response => {
          console.log(response);
          router.push("/profile/" + this.author_id);
        });
    },
    /**
     * Shows error text for given error string
     * @param error
     */
    displayError(error) {
      this.snackbar = true;
      this.snackbarText = error;
    }
  },
  watch: {
    /**
     * The function starts makes a call to the photon api once the user has types at least 3 characters and suggest different
     * cities
     * @param val of the city set in the vuetify component
     */
    search (val) {
      if(val.length < 3){
        return
      }
      this.isLoading = true

      fetch("https://photon.komoot.de/api/?q=" + val)
          .then(res => res.json())
          .then(res => {
            const { features } = res;
            this.features = this.removeNullCities(features)
          })
          .catch(err => {
            console.log(err)
          })
          .finally(() => (this.isLoading = false))
    },
    /**
     * The function starts makes a call to the photon api once the user has types at least 3 characters and suggest different
     * states
     * @param val of the state set in the vuetify component
     */
    searchState (val) {

      if(val.length < 3){
        return
      }
      this.stateLoading = true

      fetch("https://photon.komoot.de/api/?q=" + val)
          .then(res => res.json())
          .then(res => {
            const { features } = res;
            this.features = this.removeNullState(features)
          })
          .catch(err => {
            console.log(err)
          })
          .finally(() => (this.stateLoading = false))
    }
  },
};
</script>
