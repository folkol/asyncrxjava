const http = require('http');
const fs = require('fs');

const latency = 500;
const port = 1234;

function id() {
    return Math.random().toString(36).toString();
}

function mockList(length) {
    return Array.from({length: length}, id);
}

function mockContent() {
    return {
        aspects: {
            [id()]: { [id()]: id(), [id()]: id(), [id()]: id() },
            [id()]: { [id()]: id(), [id()]: id(), [id()]: id() },
            [id()]: { [id()]: id(), [id()]: id(), [id()]: id() },
            [id()]: { [id()]: id(), [id()]: id(), [id()]: id() },
        }
    };
}

function handler(req, res) {
    setTimeout(() => {
        console.log(req.url);
        res.writeHead(200, {'Content-Type': 'text/plain'});
        if (req.url === '/') {
            res.end(JSON.stringify(mockList(100)));
        } else if(req.url.startsWith('/history')) {
            res.end(JSON.stringify(mockList(20)));
        } else {
            res.end(JSON.stringify(mockContent()));
        }
    }, latency);
}

http.createServer(handler).listen(port);
fs.readFile('banner.txt', 'utf8', (_, data) => console.log(data));
