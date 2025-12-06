import axios from "axios";
// Load environment variables from .env file for local development
import 'dotenv/config';
const AUTH0_DOMAIN = process.env.AUTH0_DOMAIN;
const AUTH0_CLIENT_ID = process.env.AUTH0_CLIENT_ID
const API_BASE_URL = process.env.API_BASE_URL;

// Auth0 signup endpoint documentation: see https://auth0.com/docs/libraries/custom-signup#using-the-api
async function signup(
  email,
  password,
  firstName = null,
  lastName = null,
  cookies
) {
  var options = {
    method: "post",
    url: `https://${AUTH0_DOMAIN}/dbconnections/signup`,
    data: {
      client_id: AUTH0_CLIENT_ID,
      email: email,
      password: password,
      connection: "Username-Password-Authentication",
      // user_metadata can be used to store additional user information
    },
  };

  if (firstName && firstName.length > 0) {
    options.data.given_name = firstName;
  }

  if (lastName && lastName.length > 0) {
    options.data.family_name = lastName;
  }

  try {
    const response = await axios(options);
    
    // wait 2 seconds. Explanation: The user roles are set automatically on signup,
    // but we have to wait a short amount of time to make sure that the roles are
    // stored in the database of auth0. Otherwise the roles may not be in the
    // userinfo object on the first login.
    await new Promise(resolve => setTimeout(resolve, 2000));
    
    return await login(email, password, cookies);
  } catch (error) {
    throw error;
  }
}
// Auth0 login endpoint
async function login(username, password, cookies) {
  var options = {
    method: "post",
    url: `https://${AUTH0_DOMAIN}/oauth/token`,
    data: {
      grant_type: "password",
      username: username,
      password: password,
      audience: `https://${AUTH0_DOMAIN}/api/v2/`, // important to get access to userinfo endpoint and access token
      scope: "openid profile email",
      client_id: AUTH0_CLIENT_ID,
    },
  };

  try {
    // 1. Authentication with Auth0
    const response = await axios(options);
    const { id_token, access_token } = response.data;
    console.log(id_token);

    // After line 69 where you have: const { id_token, access_token } = response.data;
// Add these lines:

const payload = JSON.parse(Buffer.from(id_token.split('.')[1], 'base64').toString());
console.log("=== DECODED ID TOKEN ===");
console.log(JSON.stringify(payload, null, 2));
console.log("========================");

    // 2. Get user info from Auth0
    const userInfo = await getUserInfo(access_token);
    
    // 3. Sync User with Backend Database
    await syncUser(id_token, userInfo);  // ADD THIS LINE
    
    // 4. Set cookies
    cookies.set('jwt_token', id_token, {
      path: '/',
      maxAge: 60 * 60 * 24 * 7, // 7 days
      sameSite: 'lax',
      httpOnly: true,
      secure: process.env.NODE_ENV === 'production'
    });

    cookies.set('user_info', JSON.stringify(userInfo), {
      path: '/',
      maxAge: 60 * 60 * 24 * 7, // 7 days
      sameSite: 'lax',
      httpOnly: true,
      secure: process.env.NODE_ENV === 'production'
    });
    
    return { success: true };
  } catch (error) {
    console.error("Login error:", error.response?.data || error.message);
    throw error;
  }
}

async function getUserInfo(access_token) {
  var options = {
    method: "get",
    url: `https://${AUTH0_DOMAIN}/oauth/userinfo`,
    headers: {
      "Content-Type": "application/json",
      Authorization: "Bearer " + access_token,
    },
  };

  try {
    const response = await axios(options);
    return response.data;
  } catch (error) {
    throw error;
  }
}

async function syncUser(accessToken, userInfo) {
  try {
    // 1. Mapping the Auth0 data to DTO
    const userDto = {
      email: userInfo.email,
      name: userInfo.given_name || userInfo.nickname || userInfo.email,
      auth0Id: userInfo.sub, // Store Auth0 user ID for reference
      role: "STUDENT" // Standard role for new users (can be adjusted later)
    };

    // 2. Request to Spring Boot Backend
    await axios.post(`${API_BASE_URL}/api/user`, userDto, {
      headers: {
        'Authorization': `Bearer ${accessToken}`, // IMPORTANT: Sending the token!
        'Content-Type': 'application/json'
      }
    });
    console.log("User successfully synced with backend.");
  } catch (error) {
    // 409 Conflict is okay (User already exists), everything else is an error
    if (error.response && error.response.status === 409) {
      console.log("User already exists in backend. Sync skipped.");
    } else {
      console.error("Backend Sync Failed:", error.message);
      // Optional: throw error if login without DB entry should be forbidden
    }
  }
}

const auth = {
  signup,
  login,
};

export default auth;
