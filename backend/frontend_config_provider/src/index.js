import express from "express";
import cors from "cors";

const app = express();
app.use(express.json());
app.use(cors());

if (process.env.NODE_ENV !== "production") {
  process.env["APPSETTING_WEB_API_URL"] = "http://localhost:5000/api";
  process.env["APPSETTING_WEB_API_WS_URL"] = "ws://localhost:5000/api";
}

function getValue(key, prefix) {
  const k = `${prefix}_${key}`;
  if (k in process.env) {
    return process.env[k];
  }

  return null;
}

app.post("/api/config", async (req, res) => {
  const keys = req.body;
  const config = {
    appSettings: {},
    connStrings: {},
  };

  for (const appSetting of keys.appSettings) {
    config.appSettings[appSetting] = getValue(appSetting, "APPSETTING");
  }

  for (const connString of keys.connStrings) {
    config.connStrings[connString.name] = getValue(
      connString.name,
      connString.type
    );
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
