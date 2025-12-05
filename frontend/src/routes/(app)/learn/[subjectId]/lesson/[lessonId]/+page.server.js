import axios from "axios";
import 'dotenv/config';
import { error } from '@sveltejs/kit';

const API_BASE_URL = process.env.API_BASE_URL;

/** @type {import('./$types').PageServerLoad} */
export async function load({ params, locals }) {
    const { lessonId } = params;
    const jwt_token = locals.jwt_token;
    const headers = jwt_token ? { Authorization: `Bearer ${jwt_token}` } : {};

    try {
        const response = await axios.get(`${API_BASE_URL}/api/lesson/${lessonId}`, { headers });
        return {
            lesson: response.data
        };
    } catch (err) {
        console.error("Fehler beim Laden der Lektion:", err.message);
        throw error(404, 'Lektion nicht gefunden');
    }
}