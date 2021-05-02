require('source-map-support').install();
const express = require('express');

const app = express();
app.use(express.json())

app.get("/api/config", async (req, res) => {
  return res.json(process.env);
});

app.post("/api/config", async (req, res) => {
  const keys = req.body;
  const config = {};
  for (const key of keys) {
    if(key in process.env) {
      config[key] = process.env[key];
    }
    else {
      config[key] = null;
    }
  }

  return res.json(config);
});

const port = process.env.PORT || 3000;
app.listen(port, (err) => {
  if (err) {
    console.error(err);
  }

  if (__DEV__) {
    console.log("> in development");
  }

  console.log(`> listening on port ${port}`);
});
