package stefan.buciu.environment;

public enum ConnStringType {
    SQL {
        @Override
        public String toString() {
            return "SQLCONNSTR";
        }
    },
    SQL_AZURE {
        @Override
        public String toString() {
            return "SQLAZURECONNSTR";
        }
    },
    MYSQL {
        @Override
        public String toString() {
            return "MYSQLCONNSTR";
        }
    },
    POSTGRESQL {
        @Override
        public String toString() {
            return "POSTGRESQLCONNSTR";
        }
    },
    CUSTOM {
        @Override
        public String toString() {
            return "CUSTOMCONNSTR";
        }
    }
}
