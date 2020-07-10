package retrochat.com.vo;


public class Room {
	
	public int getCnt() {
		return cnt;
	}

	@Override
	public String toString() {
		return "[ROOM " + id + "번] " + name + " ( IN " + cnt + " )";
	}

	private int id;
	private int cnt;
	private String name;
	private String info;
	
	public Room(int id, int cnt, String name, String info) {
		this.id = id;
		this.cnt = cnt;
		this.name = name;
		this.info = info;
	}
	
	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getInfo() {
		return info;
	}
	
	

}
