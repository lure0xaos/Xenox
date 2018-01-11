package gargoyle.xenox.game.sys;

import gargoyle.xenox.util.config.Config;
import gargoyle.xenox.util.log.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class HiScore extends Config {
    public static final int LINES = 10;
    private static final Pattern DIGITS = Pattern.compile("[0-9]+");
    private static final String EMPTY = "";
    private static final String PARAM_NAME = "name";
    private static final String PARAM_SCORE = "score";
    private static final long serialVersionUID = -8306160552821206611L;

    public Record get(int index) {
        String key = String.valueOf(index);
        return new Record(node(key).get(PARAM_NAME, EMPTY), node(key).getInt(PARAM_SCORE, 0));
    }

    public List<Record> getRecords() {
        int size = size();
        List<Record> records = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            records.add(get(i));
        }
        return Collections.unmodifiableList(records);
    }

    public String getUserName() {
        return get(PARAM_NAME, EMPTY);
    }

    public void setUserName(String username) {
        put(PARAM_NAME, username);
    }

    private void put(int index, String name, int score) {
        Preferences node = node(String.valueOf(index));
        node.put(PARAM_NAME, name);
        node.putInt(PARAM_SCORE, score);
    }

    public void score(String name, int score) {
        put(size(), name, score);
        List<Record> table = IntStream.range(0, size()).mapToObj(this::get).sorted().collect(Collectors.toList());
        IntStream.range(0, Math.min(LINES, table.size())).forEach(index -> put(index, table.get(index).getName(), table.get(index).getScore()));
    }

    public int size() {
        int size = 0;
        try {
            for (String key : childrenNames()) {
                if (DIGITS.matcher(key).matches()) {
                    size = Math.min(LINES, Integer.parseInt(key) + 1);
                    String name = node(key).get(PARAM_NAME, EMPTY);
                    if (name != null && name.isEmpty()) {
                        break;
                    }
                }
            }
        } catch (BackingStoreException e) {
            Log.error(e.getLocalizedMessage(), e);
        }
        return size;
    }

    public static final class Record implements Serializable, Comparable<Record> {
        private final String name;
        private final int score;

        private Record(String name, int score) {
            this.name = name;
            this.score = score;
        }

        @Override
        public int compareTo(Record o) {
            return Integer.compare(o.score, score);
        }

        public String getName() {
            return name;
        }

        public int getScore() {
            return score;
        }

        @Override
        public int hashCode() {
            int prime = 31;
            int result = 1;
            result = prime * result + (name == null ? 0 : name.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            Record other = (Record) obj;
            return name == null ? other.name == null : name.equals(other.name);
        }
    }
}
