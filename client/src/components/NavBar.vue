<template>
    <header>
        <router-link to="/logout" v-if="user.isLogin"> 
            <button id="headerNavButton" class="login" v-on:click="logout">
                Logout
            </button>
        </router-link>

        <router-link to="/profile" v-if="user.isLogin">
            <button id="headerNavButton" class="myaccount">
                Profile
            </button>
        </router-link>

        <router-link to="/Signup" v-if="!user.isLogin">
            <button id="headerNavButton" class="signup">
                Sign Up 
            </button>
        </router-link>

        <router-link to="/Login" v-if="!user.isLogin"> 
            <button id="headerNavButton" class="login"> 
                Login
            </button> 
        </router-link>
    </header>
</template>
    
<script>
    import { mapGetters, mapActions } from 'vuex';

    export default {
        name: "NavBar",
        computed: {
            ...mapGetters(['user'])
        },
        methods: {
            ...mapActions(['logout'])
        }
    }
</script>

<style scoped>
    * {
        padding: 0;
        margin: 0;
    }

    header {
        padding-top: 0;
        padding-bottom: 15px;
        left: 0;
        top:0;
        width: 100%;
        position: fixed;
        background: #ffffff;
        padding: 10px 0;
        color: #fff;
        z-index: 100;
        box-shadow: 0 3px 4px 0 rgba(0,0,0,0.14), 0 3px 3px -2px rgba(0,0,0,0.12), 0 1px 8px 0 rgba(0,0,0,0.20);
    }

    body {
        margin: 0;
        padding: 0;
    }

    #headerNavButton{
        background-color: #1dca92;
        padding: 8px 18px;
        border-radius: 3px;
        border: none;
        float: right;
        color: white;
        font-size: 15px;
        margin-right: 10px;
        font-weight: 500;
        cursor: pointer;
        border: 2px solid #1dca92;
        -webkit-transition: all .2s ease;
        transition: all .2s ease;
        border-radius: 100px;
    }

    #headerNavButton:hover{
        background: #fff;
        color:#1dca92;
    }

</style>
