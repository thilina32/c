import express from 'express';
import { GoogleGenerativeAI } from '@google/generative-ai';

const app = express();
// Accept any file type (image or txt) directly
app.use(express.raw({ type: '*/*', limit: '50mb' }));

const ln = "AV0EonkptIXqrM9qjbnu5YAmbK1yqw"
const genAI = new GoogleGenerativeAI("AQ.Ab8RN6ILUy3QdlbotIQ3"+ln);

app.post('/process', async (req, res) => {
    try {
        const mimeType = req.headers['content-type'];
        const fileData = req.body; // Raw file data

        // Using the latest pro model 🚀
        const model = genAI.getGenerativeModel({ model: "gemini-3.5-flash-lite" });
        
        const prompt = "You are an expert Java programmer. Read the problem provided carefully. Identify if the concept requires Composition, Aggregation, Inheritance, or any other OOP concepts. Write the complete, fully working Java code to solve it in a single file. Return ONLY the raw Java code without any markdown formatting, ```java, or explanations.";

        let result;
        
        if (mimeType === 'text/plain') {
            // Handle text files
            const text = fileData.toString('utf-8');
            result = await model.generateContent([prompt, text]);
        } else if (mimeType.startsWith('image/')) {
            // Handle image files
            const imagePart = {
                inlineData: {
                    data: fileData.toString('base64'),
                    mimeType: mimeType
                }
            };
            result = await model.generateContent([prompt, imagePart]);
        } else {
            return res.status(400).send("Invalid file format!");
        }

        // Clean up the markdown from the response
        let code = result.response.text();
        code = code.replace(/```java\n/g, '').replace(/```java/g, '').replace(/```/g, '').trim();
        
        res.send(code);
    } catch (error) {
        // Now printing the exact error in English for debugging! 🔥
        console.error("❌ Backend Error Occurred:", error);
        res.status(500).send("Error");
    }
});

app.listen(3000, () => {
    console.log('🚀 Backend server is running on port 3000, Thilina!');
});
