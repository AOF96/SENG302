<template>
<div>
  <div class="searchUserWrapper">
    <v-snackbar outlined color="error" :timeout="timeout" v-model="snackbar" top>{{errorMessage}}</v-snackbar>
    <v-container lighten-5>
      <v-row no-gutters style="flex-wrap: nowrap;">
        <v-col cols="5" style="min-width: 300px; max-width: 100%;" class="flex-grow-1 flex-shrink-0">
          <v-card class="ma-2" style="border-radius:14px;padding:8px;">
            <h1 class="searchHeading">Search for a User</h1>
            <form>
              <v-col>
                <v-text-field id="searchQueryInput" v-on:keyup="submitSearch" label="Search" v-model="searchedTerm" outlined rounded clearable hide-details dense></v-text-field>
              </v-col>
              <v-col>
                <v-select v-model="searchBy" :items="searchMethods" name="searchValue" required label="Search By" outlined hide-details dense rounded>
                </v-select>
              </v-col>
              <v-col>
                <v-btn v-on:click="searchUsers(defaultPage, defaultSize)" color="#1cca92" outlined block rounded large>Submit</v-btn>
              </v-col>
            </form>
            <v-row class="searchRow">
              <v-list-item v-on:click="getUser(user.email)" two-line v-for="user in allUsers" :key="user.email" link>
                <v-list-item-content>
                  <v-list-item-title>{{ user.firstname + " " + user.middlename + " " + user.lastname}}</v-list-item-title>
                  <v-list-item-subtitle>{{ user.email }}</v-list-item-subtitle>
                </v-list-item-content>
              </v-list-item>
            </v-row>
            <v-row class="searchRow">
              <v-spacer />
              <v-btn
                      v-on:click="searchUsers(currentPage + 1, currentSize)"
                      :hidden="moreHidden"
                      :loading="loading"
                      :disabled="disabled"
                      color="#1cca92"
                      outlined
                      rounded
                      class="searchMoreButton"
              >
                More Results</v-btn>
              <v-spacer />
            </v-row>
          </v-card>
        </v-col>
        <v-col cols="1" style="min-width: 300px; max-width: 100%;" class="flex-grow-1 flex-shrink-0">
          <v-card class="ma-2" style="border-radius:14px;padding:8px 15px;">
            <h1 class="searchHeading" style="margin-bottom:22px;">Filter by activity</h1>
            <v-combobox v-model="activity_types_selected" :items="activities_option" chips outlined rounded label="Activity Type Select" multiple v-on:change="searchUsers(defaultPage, defaultSize)">
              <template v-slot:selection="data">
                <v-chip v-bind="data.attrs" :input-value="data.selected" close @click="data.select" @click:close="remove(data.item)">
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
            <v-radio-group v-model="filterMethod" :mandatory="true" v-on:change="searchUsers(defaultPage, defaultSize)">
              <v-radio label="Results including all" value="and"></v-radio>
              <v-radio label="Results including one of" value="or"></v-radio>
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
  apiUser
} from "../api";

export default {
  name: "searchUser",
  computed: {
    ...mapState(["user", "userSearch"]),
    ...mapGetters(["user", "userSearch"]),
  },
  data: function() {
    return {
      searchedTerm: "",
      searchBy: "fullname",
      allUsers: [],
      defaultPage: 1,
      currentPage: 1,
      defaultSize: 10,
      currentSize: 10,
      loading: false,
      disabled: false,
      moreHidden: true,
      errorMessage: null,
      snackbar: false,
      timeout: 2000,
      activities_option: [],
      activity_types_selected: [],
      searchInput: "",
      selected_activity: "Activity Type",
      filterMethod: "and",
      searchMethods: ["fullname", "lastname", "email"]
    }
  },
  mounted() {
    this.initialiser();
    const element = this.$el.querySelector('#searchQueryInput');
    if (element) this.$nextTick(() => { element.focus() });
  },
  watch: {
    "$route.params": {
      handler() {
        this.loadUrlQuery();
      }
    }
  },
  methods: {
    ...mapActions(["setUserSearch", "setScrollPosition"]),
    /**
     * Search for size amount of users on given page and append to list
     *
     * @param page Current page in results
     * @param size Size of results to retrieve
     */
    searchUsers(page, size) {
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

      /* Search for users */
      let searchTermInt = this.searchedTerm;
      if (this.searchedTerm === null || this.searchedTerm.trim().length === 0) {
        searchTermInt = null
      }
      apiUser.searchUsers(searchTermInt, this.searchBy, this.activityListToString(), this.filterMethod, page - 1, size).then(
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
     * The method changes a list of activity type to a string, which is appended to the URL for searching users
     */
    activityListToString() {
      if(this.activity_types_selected.length === 0){
        return null;
      }
      let activityString = "";
      for (let activity of this.activity_types_selected) {
        activityString += activity + ' ';
      }
      return activityString.slice(0, activityString.length - 1);

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

    remove(item) {
      const index = this.activity_types_selected.indexOf(item);
      if (index >= 0) this.activity_types_selected.splice(index, 1);
      this.searchUsers(this.defaultPage, this.defaultSize);
    },

    /**
     * Initialises search query either by url or history
     */
    initialiser(){
      if (typeof this.$route.params.query !== 'undefined' && this.$route.params.query !== null && this.$route.params.query != ""){
        this.loadUrlQuery();
      }else if(this.userSearch.searchTerm !== null){
        this.loadPreviousSearch();
      }
    },

    // Submit search query on enter pressed.
    submitSearch: function(e) {
      if (e.keyCode === 13) {
        this.searchUsers(this.defaultPage, this.defaultSize);
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
      apiUser.searchUsers(searchTermInt, this.searchBy, this.activityListToString(), this.filterMethod, 0, this.userSearch.size * this.userSearch.page).then(
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
     * Load query from url
     */
    loadUrlQuery() {
      if (typeof this.$route.params.query !== 'undefined' && this.$route.params.query !== null && this.$route.params.query != ""){
        this.searchedTerm = this.$route.params.query;
        this.$router.replace('/search');
        this.activity_types_selected = [];
        this.searchUsers(1, this.currentSize);
        const element = this.$el.querySelector('#searchQueryInput')
        if (element) this.$nextTick(() => { element.focus() })
      }
    },
    scrollWindow() {
      window.scrollTo(0, this.userSearch.scrollPos);
    }
  },
  created: async function() {
    /**
     * The function below gets all the activity types saved in the database
     */
    await apiUser
      .getActivityTypes()
      .then(response => {
        this.activities_option = response.data;
      })
      .catch(error => console.log(error));
  },
}
</script>

<style scoped>
@import "../../public/styles/pages/searchUserStyle.css";
@import "../../public/styles/pages/profileStyle.css";
</style>
