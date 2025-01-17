BEGIN TRANSACTION;
CREATE TABLE IF NOT EXISTS "poi" (
	"Latitude"	TEXT,
	"Longitude"	TEXT,
	"Poi_type"	TEXT
);
INSERT INTO "poi" VALUES ('50.342130','570049','pothole');
INSERT INTO "poi" VALUES ('','','');
INSERT INTO "poi" VALUES ('','','');
INSERT INTO "poi" VALUES ('50.342130','7.570049','pothole');
INSERT INTO "poi" VALUES ('50.343089','7.573686','bump');
INSERT INTO "poi" VALUES ('50.342699','7.578771','50');
INSERT INTO "poi" VALUES ('50.346735','7.583280','radar_70');
INSERT INTO "poi" VALUES ('50.349740','7.583033','90');
INSERT INTO "poi" VALUES ('50.353307','7.588301','radar_120');
INSERT INTO "poi" VALUES ('50.356778','7.589052','pothole');
INSERT INTO "poi" VALUES ('50.360591','7.588226','50');
INSERT INTO "poi" VALUES ('50.364821','7.588065','radar_50');
INSERT INTO "poi" VALUES ('50.369646','7.587475','radar_70');
INSERT INTO "poi" VALUES ('50.374135','50.374135','120');
COMMIT;
