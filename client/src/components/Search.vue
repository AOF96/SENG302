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
         <v-row class="searchRow" align="center" justify="center">
           <v-col>
             <input placeholder="Search"
                         type="text"
                         class="inputSearchBox"
                         v-model="searchedTerm"
              >
           </v-col>
           <v-col>
             <select v-model="searchBy" name="searchValue" class="searchDropdownMenu" required>
               <option value="Search by" selected disabled hidden>Search by</option>
               <option value="fullname">Full Name</option>
               <option value="lastname">Last Name</option>
               <option value="email">E-mail</option>
             </select>
           </v-col>
           <v-col>
             <v-btn
                 v-on:click="searchUsers(defaultPage, defaultSize)"
                 color="#1cca92"
                 outlined
                 rounded
             >Submit</v-btn>
           </v-col>
         </v-row>
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
   </div>
</template>

<script>
    import { mapGetters, mapState, mapActions } from "vuex";
    import NavBar from "./modules/NavBar";

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
        }
      },
      mounted() {
        this.loadPreviousSearch();
      },
      methods:{
        ...mapActions(["setUserSearch", "setScrollPosition", "searchForUsers", "getIdByEmail"]),
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
            this.searchForUsers({'searchTerm': this.searchedTerm, 'searchBy': this.searchBy, 'page': page - 1, 'size': size}).then(
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
            this.getIdByEmail({'email': email}).then(
                (response) => {
                  this.setScrollPosition({scrollPos: window.scrollY});
                  this.$router.push({path:"/profile/" + response.data.id})
            })
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

            this.searchForUsers({'searchTerm': this.searchedTerm, 'searchBy': this.searchBy, 'page': 0, 'size': this.userSearch.size * this.userSearch.page}).then(
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
      }
    }
</script>

<style scoped>
    @import "../../public/styles/pages/searchUserStyle.css";
</style>