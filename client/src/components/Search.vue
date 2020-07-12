<template>
   <div>
     <nav-bar></nav-bar>
     <div class="searchUserWrapper">
       <v-snackbar
           outlined
           color="error"
           :timeout="timeout"
           v-model="snackbar"
           top
       >
         {{errorMessage}}
       </v-snackbar>
       <v-container fluid class="searchUserContainer">

         <v-row class="searchRow">
           <v-spacer/>
           <h1>Search for a user</h1>
           <v-spacer/>
         </v-row>

         <form>
             <v-col cols="2"></v-col>
               <v-col>
                 <v-text-field label="Search"
                    v-model="searchedTerm"
                    outlined
                    rounded
                    clearable
                    hide-details
                  ></v-text-field>
               </v-col>
               <v-col>
                 <v-select v-model="searchBy" :items="searchMethods" name="searchValue" required
                 label="Search By"
                 outlined
                 hide-details
                 rounded>
                 </v-select>
               </v-col>
               <v-col>
                 <v-btn
                     v-on:click="searchUsers(defaultPage, defaultSize)"
                     color="#1cca92"
                     outlined
                     block
                     rounded
                     large
                 >Submit</v-btn>
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
           <v-spacer/>
           <v-btn
               v-on:click="searchUsers(currentPage + 1, currentSize)"
               :hidden="moreHidden"
               :loading="loading"
               :disabled="disabled"
               color="#1cca92"
               outlined
               rounded
               class="searchMoreButton"
           >More Results</v-btn>
           <v-spacer/>
         </v-row>
       </v-container>
     </div>
       <div>
           <v-container fluid class="rightSidebarContainer">

               <v-row class="searchRow">
                   <v-spacer/>
                   <h1>Search for a activity type</h1>
                   <v-spacer/>
               </v-row>
               <div class="addedActivityTypeContainer">
                   <v-autocomplete
                           v-model="activity_types_selected"
                           :items="activities_option"
                           filled
                           chips
                           color="blue-grey lighten-2"
                           label="Activity Type Select"
                           multiple
                   >
                       <template v-slot:selection="data">
                           <v-chip
                                   v-bind="data.attrs"
                                   :input-value="data.selected"
                                   close
                                   @click="data.select"
                                   @click:close="remove(data.item)"
                           >
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
                   </v-autocomplete>
                   <label v-bind:class="{ activeFilter: !switch1}"> OR </label>
                   <v-switch dense
                             v-model="switch1"
                   ></v-switch>
                   <label v-bind:class="{ activeFilter: switch1}">AND</label>
               </div>
           </v-container>
       </div>
       <div>
       </div>
   </div>
</template>

<script>
    import { mapGetters, mapState, mapActions } from "vuex";
    import NavBar from "./modules/NavBar";
    import {apiUser} from "../api";

    export default {
      name: "searchUser",
      components: {NavBar},
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
          activities_option:[],
          activity_types_selected: [],
          searchInput: "" ,
          selected_activity: "Activity Type",
          switch1: true,
          searchMethods: ["fullname", "lastname", "email"]
        }
      },
      mounted() {
        this.loadPreviousSearch();
      },
      methods:{
        ...mapActions(["setUserSearch", "setScrollPosition"]),
        /**
         * Search for size amount of users on given page and append to list
         *
         * @param page Current page in results
         * @param size Size of results to retrieve
         */
        searchUsers(page, size){
          if (page === this.defaultPage) {
            this.allUsers = [];
          }
          if(this.searchedTerm.trim() === ""){
              alert("Input or search box cannot be empty.")
          } else {
            /* Adjust search position */
            this.currentSize = size;
            this.currentPage = page;

            /* Change button animation */
            this.moreHidden = false;
            this.loading = true;
            this.disabled = true;

            /* Search for users */
            apiUser.searchUsers(this.searchedTerm, this.searchBy, "OR", page - 1, size).then(
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
        getUser(email){
            apiUser.getIdByEmail(email).then(
                (response) => {
                  this.setScrollPosition({scrollPos: window.scrollY});
                  this.$router.push({path:"/profile/" + response.data.id})
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
           * Removes activity type from selection
           */
          updateSwitch(){
              // const  l =   document.getElementById("filter")
              // console.log(l);
              console.log(this.switch1)
              // if(this.switch1 === "OR") {
              //     this.switch1 = "AND"
              // }
              // else{
              //     this.switch1 = "OR"
              // }
          },

          remove (item) {
              const index = this.activity_types_selected.indexOf(item)
              if (index >= 0) this.activity_types_selected.splice(index, 1)
          },
        /**
         * Researches the last search done if one exists and updates the search parameters.
         */
        loadPreviousSearch() {
          if (this.userSearch.searchTerm !== null) {
            /* Adjust search position */
            this.searchedTerm = this.userSearch.searchTerm;
            this.searchBy = this.userSearch.searchType;
            this.currentSize = this.userSearch.size;
            this.currentPage = this.userSearch.page;

            /* Change button animation */
            this.moreHidden = false;
            this.loading = true;

            apiUser.searchUsers(this.searchedTerm, this.searchBy, "OR", 0, this.userSearch.size * this.userSearch.page).then(
                (response) => {
                  if (response.data.content.size === 0) {
                    this.disabled = true;
                    this.loading = false;
                    this.errorMessage = "No more results";
                    this.snackbar = true;
                  } else {
                    this.allUsers = this.allUsers.concat(response.data.content);
                    this.loading = false;
                    this.disabled = false;
                    window.scrollTo(0, this.userSearch.scrollPos);
                  }
              }).catch(
                (error) => {
                  this.disabled = true;
                  this.loading = false;
                  this.errorMessage = error.response.data;
                  this.snackbar = true;
                })
          }
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
                    console.log(this.activities_option);
                })
                .catch(error => console.log(error));
        },
    }
</script>

<style scoped>
    @import "../../public/styles/pages/searchUserStyle.css";
    @import "../../public/styles/pages/profileStyle.css";
</style>
