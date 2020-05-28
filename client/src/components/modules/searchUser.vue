<template>
   <div>
     <nav-bar></nav-bar>
       <div class="searchUserWrapper">
           <h1>Search for a user</h1><br>
            <div class="searchUserContainer">
                <input placeholder="Search"
                       type="text"
                       class="inputSearchBox"
                       v-model="searchedTerm"
                >

                <select class="searchDropdownMenu" v-model="searchBy" name="searchValue" placeholder="Search Value" value="Search by" required>
                    <option value="Search by" selected disabled hidden>Search by</option>
                    <option value="fullname">Full Name</option>
                    <option value="lastname">Last Name</option>
                    <option value="email">E-mail</option>
                </select>
                <button type="submit" v-on:click="searchUsers()">Submit</button>

                <div>
                <v-simple-table>
                    <template v-slot:default>
                        <thead>
                        <tr>
                            <th class="text-left">Name</th>
                            <th class="text-left">Nickname</th>
                            <th class="text-left">Email</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr v-for="user in allUsers" :key="user.email">
                            <td>{{ user.firstname + " " + user.middlename + " " + user.lastname}}</td>
                            <td>{{ user.nickname }}</td>
                            <td>{{ user.email }}</td>
                        </tr>
                        </tbody>
                    </template>
                </v-simple-table>
                </div>

<!--                <img class="searchIcon" src="data:image/svg+xml;utf8;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iaXNvLTg4-->
<!--           NTktMSI/Pgo8IS0tIEdlbmVyYXRvcjogQWRvYmUgSWxsdXN0cmF0b3IgMTkuMC4wLCBTVkcgRXhwb3J0IFBsdWctSW4gLiBTVkcgVmVyc2lvb-->
<!--           jogNi4wMCBCdWlsZCAwKSAgLS0+CjxzdmcgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB4bWxuczp4bGluaz0iaHR0cDovL3-->
<!--           d3dy53My5vcmcvMTk5OS94bGluayIgdmVyc2lvbj0iMS4xIiBpZD0iQ2FwYV8xIiB4PSIwcHgiIHk9IjBweCIgdmlld0JveD0iMCAwIDU2Ljk-->
<!--           2NiA1Ni45NjYiIHN0eWxlPSJlbmFibGUtYmFja2dyb3VuZDpuZXcgMCAwIDU2Ljk2NiA1Ni45NjY7IiB4bWw6c3BhY2U9InByZXNlcnZlIiB3-->
<!--           aWR0aD0iMTZweCIgaGVpZ2h0PSIxNnB4Ij4KPHBhdGggZD0iTTU1LjE0Niw1MS44ODdMNDEuNTg4LDM3Ljc4NmMzLjQ4Ni00LjE0NCw1LjM5N-->
<!--           i05LjM1OCw1LjM5Ni0xNC43ODZjMC0xMi42ODItMTAuMzE4LTIzLTIzLTIzcy0yMywxMC4zMTgtMjMsMjMgIHMxMC4zMTgsMjMsMjMsMjNjNC-->
<!--           43NjEsMCw5LjI5OC0xLjQzNiwxMy4xNzctNC4xNjJsMTMuNjYxLDE0LjIwOGMwLjU3MSwwLjU5MywxLjMzOSwwLjkyLDIuMTYyLDAuOTIgIGM-->
<!--           wLjc3OSwwLDEuNTE4LTAuMjk3LDIuMDc5LTAuODM3QzU2LjI1NSw1NC45ODIsNTYuMjkzLDUzLjA4LDU1LjE0Niw1MS44ODd6IE0yMy45ODQs-->
<!--           NmM5LjM3NCwwLDE3LDcuNjI2LDE3LDE3cy03LjYyNiwxNy0xNywxNyAgcy0xNy03LjYyNi0xNy0xN1MxNC42MSw2LDIzLjk4NCw2eiIgZmlsb-->
<!--           D0iIzAwMDAwMCIvPgo8Zz4KPC9nPgo8Zz4KPC9nPgo8Zz4KPC9nPgo8Zz4KPC9nPgo8Zz4KPC9nPgo8Zz4KPC9nPgo8Zz4KPC9nPgo8Zz4KPC-->
<!--           9nPgo8Zz4KPC9nPgo8Zz4KPC9nPgo8Zz4KPC9nPgo8Zz4KPC9nPgo8Zz4KPC9nPgo8Zz4KPC9nPgo8Zz4KPC9nPgo8L3N2Zz4K" />-->
            </div>
       </div>
   </div>
</template>

<script>
    import { mapGetters, mapState } from "vuex";
    import NavBar from "./NavBar";
    import {apiUser} from "../../api";
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
                searchBy: "Search by",
                allUsers: {}
            }
        },
        methods:{
            searchUsers(){
                if(this.searchedTerm.trim() === ""){
                    alert("Input or search box cannot be empty.")
                }
                apiUser.searchedUser(this.searchedTerm, this.searchBy).then((response) => {
                    this.allUsers = response.content;
                    console.log(this.allUsers);
                })
                // console.log(this.allUsers.data);


            }
        }
    }
</script>

<style scoped>
    @import "../../../public/styles/pages/searchUserStyle.css";
</style>