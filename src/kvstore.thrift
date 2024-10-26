namespace java gen_java

service KeyValueStore {
  string get(1: string key),
  void put(1: string key, 2: string value),
  void delete(1: string key),
}
