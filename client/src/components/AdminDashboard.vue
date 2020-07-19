<template>
    <div>
        <div>
            <button  id = "openButton" class="openbtn" v-on:click="openSearch()">Open Search Box</button>
        </div>
        <div id="myOverlay" class="overlay">
            <span class="closebtn" v-on:click="closeSearch()" title="Close Overlay">x</span>
            <div class="overlay-content">
                <input type="text"   v-model="searchedUser.profile_id" placeholder="User Id...." name="search">
                <button type="submit" v-on:click="goToSearchedUser()">Submit</button>
            </div>
        </div>
    </div>
</template>

<script>

    import {apiUser} from "../api";
    // import json from "../../../public/json/data.json";
    // import axios from 'axios'
    // import router from "../router";
    export default {
        name: "AdminDashboard",
        data: function() {
            return {
                searchedUser: {}
            }
        },
        methods:{
            openSearch() {
                document.getElementById("myOverlay").style.display = "block";
            },
             closeSearch() {
                document.getElementById("myOverlay").style.display = "none";
            },
            async goToSearchedUser() {
                var tempSearchedUser = await apiUser.getUserById(this.searchedUser.profile_id)
                console.log(tempSearchedUser)
                if(tempSearchedUser ==  "Invalid permissions" || tempSearchedUser.permission_level == 2){
                    alert("User does not exist");
                }
                else{
                    this.$router.push('/profile/'+this.searchedUser.profile_id);
                }
                //console.log("User does not exist");
            }
        }
    }
</script>

<style scoped>

    /* The overlay effect with black background */
    .overlay {
        height: 100%;
        width: 100%;
        display: none;
        position: fixed;
        z-index: 1;
        top: 0;
        left: 0;
        background-color: grey;
        background-color: rgba(0,0,0, 0.9); /* Black with a little bit see-through */
    }

    /* The content */
    .overlay-content {
        position: relative;
        top: 46%;
        width: 80%;
        text-align: center;
        margin-top: 30px;
        margin: auto;
    }

    /* Close button */
    .overlay .closebtn {
        position: absolute;
        top: 20px;
        right: 45px;
        font-size: 60px;
        cursor: pointer;
        color: white;
    }

    .overlay .closebtn:hover {
        color: #ccc;
    }

    /* Style the search field */
    .overlay input[type=text] {
        padding: 15px;
        font-size: 17px;
        border: none;
        float: left;
        width: 80%;
        background: white;
    }

    .overlay input[type=text]:hover {
        background: #f1f1f1;
    }

    /* Style the submit button */
    .overlay button {
        float: left;
        width: 20%;
        padding: 15px;
        background: #ddd;
        font-size: 17px;
        border: none;
        cursor: pointer;
    }

    .overlay button:hover {
        background: #bbb;
    }

    #openButton{
        background-color: #1dca92;
        padding: 8px 18px;
        border-radius: 3px;
        border: none;
        color: white;
        font-size: 15px;
        margin: 12px;
        font-weight: 500;
        cursor: pointer;
        border: 2px solid #1dca92;
        -webkit-transition: all 0.2s ease;
        transition: all 0.2s ease;
        border-radius: 100px;
    }

</style>
