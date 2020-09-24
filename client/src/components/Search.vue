<template>
  <div>
    <div class="searchUserWrapper">
      <v-snackbar outlined color="error" :timeout="timeout" v-model="snackbar" top>{{errorMessage}}</v-snackbar>
      <v-container class="pa-0">
        <v-row style="flex-wrap: wrap-reverse;" no-gutters class="mainRow">
          <v-col xs="12" sm="6" class="flex-grow-1 flex-shrink-0 columnContainer">
            <v-card style="border-radius:15px;" class="ma-2 searchActivityCard">
              <form>
                <h1 style="text-align: center; color: var(--v-primaryText-base)"> Search</h1>
                <v-toolbar flat height="auto" style="background:none;">
                  <template v-slot:extension>
                    <v-tabs
                        v-model="tabs"
                        fixed-tabs
                    >
                      <v-tabs-slider></v-tabs-slider>
                      <v-tab class="searchTab" href="#mobile-tabs-5-1"
                             v-on:click="activitySearchTab = false">
                        <h1 class="searchHeading" style="color: var(--v-primaryText-base)">User</h1>
                      </v-tab>

                      <v-tab class="searchTab" href="#mobile-tabs-5-2" v-on:click="loadActivitySearchTab">
                        <h1 class="searchHeading" style="color: var(--v-primaryText-base)">Activity</h1>
                      </v-tab>
                    </v-tabs>
                  </template>
                </v-toolbar>
                <v-tabs-items v-model="tabs">
                  <v-tab-item
                      v-for="index in 2"
                      :key="index"
                      :value="'mobile-tabs-5-' + index"
                  >

                    <v-card flat>
                      <div v-if="index === 1">
                        <v-col>
                          <v-text-field id="searchQueryInput" class="queryInput" v-on:keyup="submitSearch"
                                        label="Search User" v-model="searchedTerm" outlined rounded clearable
                                        hide-details dense></v-text-field>
                        </v-col>
                        <v-col>
                          <v-select v-model="searchBy" :items="searchMethods" item-text="display" name="searchValue"
                                    required label="Search By" outlined hide-details dense rounded>
                          </v-select>
                        </v-col>
                        <v-col>
                          <v-btn v-on:click="submitButtonCheck(defaultPage, defaultSize)" color="#1cca92" outlined block
                                 rounded large>Submit
                          </v-btn>
                        </v-col>
                      </div>
                    </v-card>
                    <v-card flat>
                      <div v-if="index === 2">
                        <v-col>
                          <v-text-field id="searchActivityQueryInput" class="queryInput" v-on:keyup="submitActivitySearch"
                                        label="Search Activity"
                                        v-model="searchedActivityTerm" outlined rounded clearable hide-details
                                        dense></v-text-field>
                        </v-col>
                        <v-col>
                          <v-btn v-on:click="submitActivityButtonCheck(defaultActivityPage, defaultActivitySize, multipleActivityFilterMethod)"
                                 color="#1cca92" outlined block rounded large>Submit
                          </v-btn>
                        </v-col>
                      </div>
                    </v-card>

                  </v-tab-item>
                </v-tabs-items>
                <div v-if="!activitySearchTab">
                  <v-row class="searchRow">
                    <v-list-item v-on:click="getUser(user.email)" two-line v-for="user in allUsers" :key="user.email"
                                 link>
                      <v-list-item-content>
                        <v-list-item-title v-if="user.middlename != null">
                          {{ user.firstname + " " + user.middlename + " " + user.lastname}}
                        </v-list-item-title>
                        <v-list-item-title v-else>
                          {{ user.firstname + " " + user.lastname}}
                        </v-list-item-title>
                        <v-list-item-subtitle>{{ user.email }}</v-list-item-subtitle>
                      </v-list-item-content>
                    </v-list-item>
                  </v-row>
                  <v-row class="searchRow">
                    <v-spacer/>
                    <v-btn
                        v-on:click="searchUsers(currentPage + 1, currentSize, multipleUserSearchTermMethod)"
                        :hidden="moreHidden"
                        :loading="loading"
                        :disabled="disabled"
                        color="#1cca92"
                        outlined
                        rounded
                        class="searchMoreButton"
                    >
                      More Results
                    </v-btn>
                    <v-spacer/>
                  </v-row>
                </div>

                <div v-if="activitySearchTab">
                  <v-row class="searchRow">
                    <v-list-item v-on:click="goToActivity(activity.id)" two-line v-for="activity in allActivities"
                                 :key="activity.id" link>
                      <v-liscon-item-content>
                        <v-list-item-title>
                          {{ activity.name}}
                        </v-list-item-title>
                        <div v-if="activity.location !== null">
                          <v-list-item-subtitle>
                            {{ activity.location.street_address}}, {{ activity.location.city}}, {{
                            activity.location.country}}
                          </v-list-item-subtitle>
                        </div>
                      </v-liscon-item-content>
                    </v-list-item>
                  </v-row>
                  <v-row class="searchRow">
                    <v-spacer/>
                    <v-btn
                        v-on:click="searchActivity(currentActivityPage + 1, currentActivitySize, multipleActivityFilterMethod)"
                        :hidden="moreHidden"
                        :loading="loading"
                        :disabled="disabled"
                        color="#1cca92"
                        outlined
                        rounded
                        class="searchMoreButton"
                    >
                      More Results
                    </v-btn>
                    <v-spacer/>
                  </v-row>
                </div>
              </form>
            </v-card>
          </v-col>
          <v-col cols="1" class="flex-grow-1 flex-shrink-0 searchFilterContainer">
            <v-card style="border-radius:15px;" v-if="!activitySearchTab" class="ma-2 activityFilterCard">
              <h1 class="searchHeading activityFilterHeading" style="color: var(--v-primaryText-base)">Filter by activity</h1>
              <v-combobox v-model="activity_types_selected" :items="activities_option" chips outlined rounded
                          label="Activity Type Select" multiple
                          v-on:change="searchUsers(defaultActivityPage, defaultActivityPage, multipleUserSearchTermMethod)">
                <template v-slot:selection="data">
                  <v-chip v-bind="data.attrs" :input-value="data.selected" close @click="data.select"
                          @click:close="remove(data.item)">
                    {{ data.item }}
                  </v-chip>
                </template>
                <template v-slot:item="data">
                  <template v-if="typeof data.item !== 'object'">
                    <v-list-item-content v-text="data.item"></v-list-item-content>
                  </template>
                  <template v-else>
                    <v-list-item-content>
                      <v-list-item-title v-html="data.item"></v-list-item-title>
                    </v-list-item-content>
                  </template>
                </template>
              </v-combobox>
              <v-label>Filter method</v-label>
              <v-radio-group v-model="filterMethod" :mandatory="true"
                             v-on:change="searchUsers(defaultPage, defaultSize, multipleUserSearchTermMethod)">
                <v-radio label="Results including all" value="and"></v-radio>
                <v-radio label="Results including one of" value="or"></v-radio>
              </v-radio-group>
            </v-card>
            <v-card v-if="!activitySearchTab" :disabled="searchBy === 'fullname'" class="ma-2" style="border-radius:14px;padding:8px 15px;">
              <h1 class="searchHeading">Search using keywords</h1>
              <v-row class="ml-1">
                <v-label class="activityFilterMethodLabel">Filter method</v-label>
                <v-tooltip top max-width="500px">
                  <template v-slot:activator="{ on }">
                    <v-icon v-on="on" style="margin-left: 10px;margin-top: -3px;font-size: 20px;" class="filterIcon">mdi-help-circle-outline</v-icon>
                  </template>
                  <span class="filterMethodInfo">You can search for users using multiple keywords by selecting your
                    preferred method below and typing the keywords you want to search for separated by commas!</span>
                </v-tooltip>
              </v-row>
              <v-radio-group v-model="multipleUserSearchTermMethod" :mandatory="true">
                <v-radio label="Regular search" value="single"></v-radio>
                <v-radio label="Results including all terms" value="and"></v-radio>
                <v-radio label="Results including at least one term" value="or"></v-radio>
              </v-radio-group>
            </v-card>
            <v-card style="border-radius:15px;" v-if="activitySearchTab" class="ma-2 activityFilterCard">
              <h1 class="searchHeading activityFilterHeading" style="color: var(--v-primaryText-base)">Search using keywords</h1>
              <v-row class="ml-1">
                <v-label style="padding-right: 5px">Filter method</v-label>
                <v-tooltip top max-width="500px">
                  <template v-slot:activator="{ on }">
                    <v-icon v-on="on" style="margin-left:10px;margin-top:-3px;font-size: 20px;">mdi-help-circle-outline</v-icon>
                  </template>
                  <span class="filterMethodInfo">You can search for users using multiple keywords by selecting your
                    preferred method below and typing the keywords you want to search for separated by commas!</span>
                </v-tooltip>
              </v-row>
              <v-radio-group v-model="multipleActivityFilterMethod" :mandatory="true">
                <v-radio label="Regular search" value="single"></v-radio>
                <v-radio label="Results including all terms" value="and"></v-radio>
                <v-radio label="Results including at least one term" value="or"></v-radio>
              </v-radio-group>
            </v-card>
          </v-col>
        </v-row>
      </v-container>
    </div>
  </div>
</template>

<script>
  import {
    mapGetters,
    mapState,
    mapActions
  } from "vuex";
  import {
    apiActivity,
    apiUser
  } from "../api";

  export default {
    name: "searchUser",
    computed: {
      ...mapState(["user", "userSearch"]),
      ...mapGetters(["user", "userSearch"]),
    },
    data: function () {
      return {
        searchedTerm: "",
        searchedActivityTerm: "",
        searchBy: "fullname",
        allUsers: [],
        defaultPage: 1,
        currentPage: 1,
        defaultSize: 10,
        currentSize: 10,
        defaultActivityPage: 1,
        currentActivityPage: 1,
        defaultActivitySize: 10,
        currentActivitySize: 10,
        loading: false,
        disabled: false,
        moreHidden: true,
        activitySearchTab: false,
        allActivities: [],
        errorMessage: null,
        snackbar: false,
        timeout: 2000,
        tabs: null,
        activities_option: [],
        activity_types_selected: [],
        names_selected: [],
        searchInput: "",
        selected_activity: "Activity Type",
        filterMethod: "and",
        multipleActivityFilterMethod: "single",
        multipleUserSearchTermMethod: "single",
        userSearchTerms: [],
        searchMethods: [
          {display: "Full Name", value: "fullname"},
          {display: "Last Name", value: "lastname"},
          {display: "Email", value: "email"}
        ]
      }
    },
    mounted() {
      if (!this.user.isLogin) {
        this.$router.push('/login');
      } else {
        this.initialiser();
        const element = this.$el.querySelector('#searchQueryInput');
        if (element) this.$nextTick(() => {
          element.focus()
        });
      }
  },
  watch: {
    "$route.params": {
      handler() {
        this.loadUrlQuery();
      }
    }
  },
  methods: {
    ...mapActions(["setUserSearch", "setScrollPosition", "setActivitySearch"]),
    /**
     * Checks if the search term when looking for the user is not empty or invalid.
     * @param page Current page in results
     * @param size Size of results to retrieve
     */
    submitButtonCheck(page, size) {
      if ((this.searchedTerm === null || this.searchedTerm.trim().length === 0) && this.activity_types_selected.length === 0) {
        this.errorMessage = "Search is empty";
        this.snackbar = true;
      } else {
          this.searchUsers(page, size, this.multipleUserSearchTermMethod);
      }
    },
    /**
     * Checks if the search term when looking for an activity is not empty or invalid.
     * @param page Current page in results
     * @param size Size of results to retrieve
     * @param method determines whether the search is for a single activity or multiple with and or or
     */
    submitActivityButtonCheck(page, size, method) {
      if ((this.searchedActivityTerm === null || this.searchedActivityTerm.trim().length === 0) && this.searchedActivityTerm.length === 0) {
        this.errorMessage = "Search is empty";
        this.snackbar = true;
      } else {
        this.searchActivity(page, size, method);
      }
    },

    /**
     * Checks if more searched activity result exists and if they don't hides the more results button else shows
     * more results. Also manages the snack bar and error messages.
     * @param page Current page in results
     * @param size Size of results to retrieve
     */
    searchActivity(page, size, method) {
      if (page === this.defaultActivityPage) {
        this.allActivities = [];
      }
      /* Adjust search position */
      this.currentActivitySize = size;
      this.currentActivityPage = page;

      /* Change button animation */
      this.moreHidden = false;
      this.loading = true;
      this.disabled = true;

      if (method !== "single") {
        this.names_selected = this.searchedActivityTerm.trim().split(",");
        for (let i = 0; i < this.names_selected.length; i++) {
          this.names_selected[i] = this.names_selected[i].trim();
        }
        apiActivity.getSearchedActivity(null, this.names_selected, method, page, size).then(
            (response) => {
              if (response.data.content.length === 0) {
                this.disabled = true;
                this.loading = false;
                this.errorMessage = "No more results";
                this.snackbar = true;
              } else {
                this.loading = false;
                this.disabled = false;
                this.allActivities = this.allActivities.concat(response.data.content);
                this.setActivitySearch({
                  searchTerm: this.searchedActivityTerm,
                  page: page,
                  size: size,
                  scrollPos: window.scrollY,
                });
              }
            }).catch(
            (error) => {
              this.disabled = true;
              this.loading = false;
              this.errorMessage = error.response.data;
              this.snackbar = true;
            })
      } else {
        apiActivity.getSearchedActivity(this.searchedActivityTerm, [], method, page - 1, size).then(
            (response) => {
              if (response.data.content.length === 0) {
                this.disabled = true;
                this.loading = false;
                this.errorMessage = "No more results";
                this.snackbar = true;
              } else {
                this.loading = false;
                this.disabled = false;
                this.allActivities = this.allActivities.concat(response.data.content);
                this.setActivitySearch({
                  searchTerm: this.searchedActivityTerm,
                  page: page,
                  size: size,
                  scrollPos: window.scrollY,
                });
              }
            }).catch(
            (error) => {
              this.disabled = true;
              this.loading = false;
              this.errorMessage = error.response.data;
              this.snackbar = true;
            })
      }
    },

    /**
     * Search for size amount of users on given page and append to list
     *
     * @param page Current page in results
     * @param size Size of results to retrieve
     * @param method
     */
    searchUsers(page, size, method) {
      if ((this.searchedTerm === null || this.searchedTerm.trim().length === 0) && this.activity_types_selected.length === 0) {
        this.allUsers = [];
        this.moreHidden = true;
        return;
      }
      if (page === this.defaultPage) {
        this.allUsers = [];
      }
      /* Adjust search position */
      this.currentSize = size;
      this.currentPage = page;

        /* Change button animation */
        this.moreHidden = false;
        this.loading = true;
        this.disabled = true;

        if (method === "single") {
          /* Search for users */
          apiUser.searchUsers(this.searchedTerm, this.searchBy, this.activity_types_selected, [], this.searchBy, this.multipleUserSearchTermMethod, this.filterMethod, page - 1, size).then(
              (response) => {
                if (response.data.content.length === 0) {
                  this.disabled = true;
                  this.loading = false;
                  this.errorMessage = "No more results";
                  this.snackbar = true;
                } else {
                  this.allUsers = this.allUsers.concat(response.data.content);
                  this.loading = false;
                  this.disabled = false;
                  /* Update search history */
                  this.setUserSearch({
                    searchTerm: this.searchedTerm,
                    searchType: this.searchBy,
                    page: page,
                    size: size,
                    scrollPos: window.scrollY,
                    activityTypesSelected: this.activity_types_selected,
                    filterMethod: this.filterMethod
                  });
                }
              }).catch(
              (error) => {
                this.disabled = true;
                this.loading = false;
                this.errorMessage = error.response.data;
                this.snackbar = true;
              }
          )
        } else {
          this.userSearchTerms = this.searchedTerm.trim().split(",");
          for (let i = 0; i < this.userSearchTerms.length; i++) {
            this.userSearchTerms[i] = this.userSearchTerms[i].trim();
          }

          apiUser.searchUsers(this.searchedTerm, this.searchBy, this.activity_types_selected, this.userSearchTerms, this.searchBy, this.multipleUserSearchTermMethod, this.filterMethod, page - 1, size).then(
              (response) => {
                if (response.data.content.length === 0) {
                  this.disabled = true;
                  this.loading = false;
                  this.errorMessage = "No more results";
                  this.snackbar = true;
                } else {
                  this.allUsers = this.allUsers.concat(response.data.content);
                  this.loading = false;
                  this.disabled = false;
                  /* Update search history */
                  this.setUserSearch({
                    searchTerm: this.searchedTerm,
                    searchType: this.searchBy,
                    page: page,
                    size: size,
                    scrollPos: window.scrollY,
                    activityTypesSelected: this.activity_types_selected,
                    filterMethod: this.filterMethod
                  });
                }
              }).catch(
              (error) => {
                this.disabled = true;
                this.loading = false;
                this.errorMessage = error.response.data;
                this.snackbar = true;
              }
          )
        }

      },
      /**
       * Gets a users information from their email.
       *
       * @param email A users email
       */
      getUser(email) {
        apiUser.getIdByEmail(email).then(
            (response) => {
              this.setScrollPosition({
                scrollPos: window.scrollY
              });
              this.$router.push({
                path: "/profile/" + response.data.id
              })
            })
      },

      /**
       * Directs user to the activity that appears on the search activity results
       *
       * @param activityId the id of the activity the user wants to look into
       */
      goToActivity(activityId) {
        this.$router.push({
          path: "/activity/" + activityId
        })
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
       * Removes the activity from the activity filter component.
       **/
      remove(item) {
        const index = this.activity_types_selected.indexOf(item);
        if (index >= 0) this.activity_types_selected.splice(index, 1);
        this.searchUsers(this.defaultPage, this.defaultSize, this.multipleUserSearchTermMethod);
      },

      /**
       * Initialises search query either by url or history
       */
      initialiser() {
        if (typeof this.$route.params.query !== 'undefined' && this.$route.params.query !== null && this.$route.params.query !== "") {
          this.loadUrlQuery();
        } else if (this.userSearch !== undefined && this.userSearch.searchTerm !== null) {
          this.loadPreviousSearch();
        } else if (this.activitySearch !== undefined && this.activitySearch.searchTerm !== null) {
          this.activitySearchTab = true;
          this.tabs = "mobile-tabs-5-2";
          this.loadPreviousActivitySearch();
        }
      },

      /**
       * Submit search query on enter pressed.
       */
      submitSearch: function (e) {
        if (e.keyCode === 13) {
          this.searchUsers(this.defaultPage, this.defaultSize, this.multipleUserSearchTermMethod);
        }
      },

      /**
       * Submit search query on enter pressed.
       */
      submitActivitySearch: function (e) {
        if (e.keyCode === 13) {
          this.searchActivity(this.defaultActivityPage, this.defaultActivitySize, this.multipleActivityFilterMethod);
        }
      },

      /**
       * Researches the last search done if one exists and updates the search parameters.
       */
      loadPreviousSearch() {
        /* Adjust search position */
        this.searchedTerm = this.userSearch.searchTerm;
        this.searchBy = this.userSearch.searchType;
        this.currentSize = this.userSearch.size;
        this.currentPage = this.userSearch.page;
        this.activity_types_selected = this.userSearch.activityTypesSelected;
        this.filterMethod = this.userSearch.filterMethod;

        /* Change button animation */
        this.moreHidden = false;
        this.loading = true;

        let searchTermInt = this.searchedTerm;
        if (this.searchedTerm.trim().length === 0) {
          searchTermInt = null
        }
            apiUser.searchUsers(searchTermInt, this.searchBy, this.activity_types_selected, [], this.searchBy, this.multipleUserSearchTermMethod,  this.filterMethod, 0, this.userSearch.size * this.userSearch.page).then(
            (response) => {
              if (response.data.content.size === 0) {
                this.disabled = true;
                this.loading = false;
                this.errorMessage = "No more results";
                this.snackbar = true;
              } else {
                this.allUsers = response.data.content;
                this.loading = false;
                this.disabled = false;
                setTimeout(this.scrollWindow, 10)
              }
            }).catch(
            (error) => {
              this.disabled = true;
              this.loading = false;
              this.errorMessage = error.response.data;
              this.snackbar = true;
            })
      },

      /**
       * Loads previous activity search
       */
      loadPreviousActivitySearch() {
        /* Adjust search position */
        this.searchedActivityTerm = this.activitySearch.searchTerm;
        this.currentActivitySize = this.activitySearch.size;
        this.currentActivityPage = this.activitySearch.page;

        /* Change button animation */
        this.moreHidden = false;
        this.loading = true;
        this.disabled = true;

        let searchTermInt = this.searchedActivityTerm;
        if (this.searchedActivityTerm.trim().length === 0) {
          searchTermInt = null
        }
        apiActivity.getSearchedActivity(searchTermInt, 0, this.activitySearch.size * this.activitySearch.page).then(
            (response) => {
              if (response.data.content.length === 0) {
                this.disabled = true;
                this.loading = false;
                this.errorMessage = "No more results";
                this.snackbar = true;
              } else {
                this.loading = false;
                this.disabled = false;
                this.allActivities = response.data.content;
                setTimeout(this.scrollWindow, 10, this.activitySearch.scrollPos);
              }
            }).catch(
            (error) => {
              this.disabled = true;
              this.loading = false;
              this.errorMessage = error.response.data;
              this.snackbar = true;
            }
        )
      },

      /**
       * Tries to load previous search after switching to activity search tab
       */
      loadActivitySearchTab() {
        this.activitySearchTab = true;

        if (this.activitySearch !== undefined && this.activitySearch.searchTerm !== null) {
          this.loadPreviousActivitySearch();
        }
      },

      /**
       * Load query from url
       */
      loadUrlQuery() {
        if (typeof this.$route.params.query !== 'undefined' && this.$route.params.query !== null && this.$route.params.query !== "") {
          this.searchedTerm = this.$route.params.query;
          this.$router.replace('/search');
          this.activity_types_selected = [];
          this.searchUsers(1, this.currentSize, this.multipleUserSearchTermMethod);
          const element = this.$el.querySelector('#searchQueryInput')
          if (element) this.$nextTick(() => {
            element.focus()
          })
        }
      },

      /**
       * Scrolls window to given scroll position
       */
      scrollWindow(scrollPos) {
        window.scrollTo(0, scrollPos);
      }
    },
    created: async function () {
      /**
       * The function below gets all the activity types saved in the database
       */
      await apiUser
          .getActivityTypes()
          .then(response => {
            this.activities_option = response.data;
            for (let i = 0; i < this.activities_option.length; i++) {
              this.activities_option[i] = this.activities_option[i].replace(/-/g, " ")
            }
          })
          .catch(error => console.log(error));
    },
  }
</script>

<style scoped>
  @import "../../public/styles/pages/searchUserStyle.css";
  @import "../../public/styles/pages/profileStyle.css";
</style>