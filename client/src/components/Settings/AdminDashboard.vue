<template>
    <div>
        <NavBar />
        <div>
            <button  id = "openButton" class="openbtn" v-on:click="openSearch()">Open Search Box</button>
        </div>
        <div id="myOverlay" class="overlay">
            <span class="closebtn" v-on:click="closeSearch()" title="Close Overlay">x</span>
            <div class="overlay-content">
                <form action="action_page.php">
                    <input type="text" placeholder="User Id...." name="search">
                    <button type="submit">Submit</button>
                </form>
            </div>
        </div>
    </div>
</template>

<script>

    import NavBar from '@/components/NavBar'
    // import axios from 'axios'
    // import router from "../../router";
    export default {
        name: "AdminDashboard",
        components: {
            NavBar
        },
        methods:{
            openSearch() {
        document.getElementById("myOverlay").style.display = "block";
    },
     closeSearch() {
        document.getElementById("myOverlay").style.display = "none";
    },
            // searchUser(){
            //     axios.get()
            //    //do the get request for the user entered by admin and then push and then set the userinterface data in user.module.js
            //    // and then push the page to profile. We are here potraying as if the admin is that user, which would give it access to edit
            //    //the users information.
            //    //  router.push('Profile');
            // }
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
        margin-right: 10px;
        font-weight: 500;
        cursor: pointer;
        border: 2px solid #1dca92;
        -webkit-transition: all 0.2s ease;
        transition: all 0.2s ease;
        border-radius: 100px;
    }

</style>