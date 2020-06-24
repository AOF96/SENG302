<template>
   <div>
     <nav-bar></nav-bar>
     <div class="searchUserWrapper">
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
                 :loading="loading"
                 :disabled="loading"
                 color="#1cca92"
                 outlined
                 rounded
             >Submit</v-btn>
           </v-col>
         </v-row>
         <v-row class="searchRow">
           <v-list-item two-line v-for="user in allUsers" :key="user.email">
             <v-list-item-content>
               <v-list-item-title v-on:click="getUser(user.email)">{{ user.firstname + " " + user.middlename + " " + user.lastname}}</v-list-item-title>
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
               :disabled="loading"
               color="#1cca92"
               outlined
               rounded
           >More Results</v-btn>
           <v-spacer/>
         </v-row>
       </v-container>
     </div>
   </div>
</template>

<script>
    import { mapGetters, mapState } from "vuex";
    import NavBar from "./modules/NavBar";
    import {apiUser} from "../api";
    export default {
        name: "searchUser",
        components: {NavBar},
        computed: {
            ...mapState(["user"]),
            ...mapGetters(["user"])
        },
        data: function() {
          return {
            searchedTerm: "",
            searchBy: "fullname",
            allUsers: [],
            defaultPage: 1,
            currentPage: 1,
            defaultSize: 3,
            currentSize: 3,
            loading: false,
            moreHidden: true,
          }
        },
        methods:{
            searchUsers(page, size){
              if (page === this.defaultPage) {
                this.allUsers = [];
              }
              this.currentSize = size;
              this.currentPage = page;
              if(this.searchedTerm.trim() === ""){
                  alert("Input or search box cannot be empty.")
              } else {
                this.moreHidden = false;
                this.loading = true;
                apiUser.searchUsers(this.searchedTerm, this.searchBy, page - 1, size).then((response) => {
                  this.allUsers = this.allUsers.concat(response.data.content);
                  this.loading = false;
                })
              }
            },
            getUser(email){
                apiUser.getIdByEmail(email).then((response) =>
                    this.$router.push({path:"/profile/" + response.data.id})
                )
            }
        }
    }
</script>

<style scoped>
    @import "../../public/styles/pages/searchUserStyle.css";
</style>