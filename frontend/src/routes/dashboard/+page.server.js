import axios from "axios";
import { error} from '@sveltejs/kit';
// Load environment variables from .env file for local development
import 'dotenv/config';
const API_BASE_URL = process.env.API_BASE_URL; // defined in .env file

/** @type {import('./$types').PageServerLoad} */
export async function load({ locals }) {
    // Token from locals (in case we enable auth later)
    const jwt_token = locals.jwt_token;

    // Prepare headers (currently optional as backend is open)
    const headers = jwt_token ? { Authorization: `Bearer ${jwt_token}` } : {};

    try {
        // Load subjects and lessons in parallel for performance
        const [subjectsRes, lessonsRes] = await Promise.all([
            axios.get(`${API_BASE_URL}/api/subject`, { headers }),
            axios.get(`${API_BASE_URL}/api/lesson`, { headers })
        ]);

        return {
            subjects: subjectsRes.data,
            lessons: lessonsRes.data
        };

    } catch (error) {
        console.error("Error loading dashboard:", error.message);
        return {
            subjects: [],
            lessons: [],
            error: "Data could not be loaded."
        };
    }
}