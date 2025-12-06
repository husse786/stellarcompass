import { fail, redirect } from '@sveltejs/kit';
import auth from '$lib/server/auth.service.js';

export const actions = {
  signup: async ({ request, cookies }) => {
    const data = await request.formData();
    const email = data.get('email');
    const password = data.get('password');
    const firstName = data.get('firstName');
    const lastName = data.get('lastName');
    // validation
    if (!email || !password) {
      return fail(400, { email, error: 'E-Mail und Passwort sind erforderlich.' });
    }

    try {
      // Call the auth.js signup function - it handles cookie setting - and login after signup
      await auth.signup(email, password, firstName, lastName, cookies);
    } catch (error) {
      console.error('Signup error:', error);
      return fail(400, {
        email,
        error: 'Signup failed. Please try again.'
      });
    }
    
    // If we get here, signup was successful - redirect to dashboard
    throw redirect(303, '/dashboard');
  }
};
