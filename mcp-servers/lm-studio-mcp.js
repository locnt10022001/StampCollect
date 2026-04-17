#!/usr/bin/env node

/**
 * LM Studio MCP Bridge (Hardcoded Token Version)
 */

const fs = require('fs');

// HARDCODED CONFIGURATION
const LM_STUDIO_URL = "http://localhost:1234/v1/chat/completions";
const LM_API_TOKEN = "sk-lm-6PPf2Czo:mFFrdPb4rxeOTG52sluF";
const LOG_FILE = "/Users/iamthanhloc/mcp_debug.log";

function log(msg) {
    try {
        fs.appendFileSync(LOG_FILE, `[${new Date().toISOString()}] ${msg}\n`);
    } catch (e) {}
}

log("Bridge started (Hardcoded Version)");

async function handleRequest(request) {
    const { method, params, id } = request;
    if (!method) return null;

    if (method === 'initialize') {
        return {
            jsonrpc: "2.0",
            id,
            result: {
                protocolVersion: "2024-11-05",
                capabilities: { tools: {} },
                serverInfo: { name: "lm-studio-bridge", version: "1.2.0" }
            }
        };
    }

    if (method === 'tools/list') {
        return {
            jsonrpc: "2.0",
            id,
            result: {
                tools: [{
                    name: "ask_lm_studio",
                    description: "Gửi câu hỏi cho AI đang chạy local trong LM Studio.",
                    inputSchema: {
                        type: "object",
                        properties: {
                            prompt: { type: "string", description: "Câu hỏi cho AI." }
                        },
                        required: ["prompt"]
                    }
                }]
            }
        };
    }

    if (method === 'tools/call') {
        if (params.name === 'ask_lm_studio') {
            const prompt = params.arguments.prompt;
            try {
                const response = await fetch(LM_STUDIO_URL, {
                    method: 'POST',
                    headers: { 
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${LM_API_TOKEN}`
                    },
                    body: JSON.stringify({
                        messages: [{ role: "user", content: prompt }],
                        temperature: 0.7
                    })
                });

                if (!response.ok) {
                    const errorText = await response.text();
                    log(`API Error: ${response.status} - ${errorText}`);
                    return {
                        jsonrpc: "2.0",
                        id,
                        result: { isError: true, content: [{ type: "text", text: `LM Studio Error: ${response.status}` }] }
                    };
                }

                const data = await response.json();
                const content = data.choices[0].message.content;

                return {
                    jsonrpc: "2.0",
                    id,
                    result: { content: [{ type: "text", text: content }] }
                };
            } catch (error) {
                log(`Fetch error: ${error.message}`);
                return {
                    jsonrpc: "2.0",
                    id,
                    result: { isError: true, content: [{ type: "text", text: `Connection error: ${error.message}` }] }
                };
            }
        }
    }

    if (id === undefined) return null;
    return {
        jsonrpc: "2.0",
        id,
        error: { code: -32601, message: `Method not found: ${method}` }
    };
}

let buffer = '';
process.stdin.on('data', async (chunk) => {
    buffer += chunk.toString();
    let lines = buffer.split('\n');
    buffer = lines.pop();
    for (let line of lines) {
        if (!line.trim()) continue;
        try {
            const request = JSON.parse(line);
            const response = await handleRequest(request);
            if (response) process.stdout.write(JSON.stringify(response) + '\n');
        } catch (e) { log(`Parse error: ${e.message}`); }
    }
});

process.stdin.on('end', async () => {
    if (buffer.trim()) {
        try {
            const request = JSON.parse(buffer);
            const response = await handleRequest(request);
            if (response) process.stdout.write(JSON.stringify(response) + '\n');
        } catch (e) {}
    }
    log("Stdin closed.");
});
