package ch.dragbone.anonsomenauts;

public class BitData{
	private boolean[] data;

	public BitData(boolean[] boolData){
		data = boolData;
	}

	public BitData(byte[] byteData){
		this(byteToBoolArray(byteData));
	}

	public void replace(int pos, String rep){
		replace(pos, rep.getBytes());
	}

	public void replace(int pos, byte[] rep){
		replace(pos, byteToBoolArray(rep));
	}

	public void replace(int pos, boolean[] rep){
		for(int i = pos; i < pos + rep.length; ++i){
			data[i] = rep[i - pos];
		}
	}

	/**
	 * @param pos Position to start replacing
	 * @param length Number of bits to replace
	 * @param rep Data to replace with
	 */
	public void replace(int pos, int length, boolean[] rep){
		int diff = rep.length - length;
		boolean[] newData = new boolean[data.length + diff];
		System.arraycopy(data, 0, newData, 0, pos);
		System.arraycopy(data, pos + length, newData, pos + rep.length, data.length - pos - length);
		for(int i = pos; i < pos + rep.length; ++i){
			newData[i] = rep[i - pos];
		}
		data = newData;
	}

	public void test(String s){
		boolean[] bs = byteToBoolArray(s.getBytes());
		print(bs);
	}

	public int find(boolean[] search, int startPos){
		for(int i = startPos; i < data.length; ++i){
			for(int c = 0; i + c < data.length && data[i + c] == search[c]; ++c){
				if(c == search.length - 1){
					return i;
				}
			}
		}
		return -1;
	}

	public int findFirst(boolean[] search){
		return find(search, 0);
	}

	public int find(byte[] search, int startPos){
		return find(byteToBoolArray(search), startPos);
	}

	public int findFirst(byte[] search){
		return find(search, 0);
	}

	public int find(String search, int startPos){
		return find(search.getBytes(), startPos);
	}

	public int findFirst(String search){
		return find(search, 0);
	}

	public void find(byte[] search){
		boolean[] bSearch = byteToBoolArray(search);
		for(int i = 0; i < data.length; ++i){
			for(int c = 0; i + c < data.length && data[i + c] == bSearch[c]; ++c){
				if(c == bSearch.length - 1){
					System.out.println("Found @" + i + " [offset:" + (i % 8) + "]");
					break;
				}
			}
		}
	}

	public byte[] toByteArray(){
		byte[] ba = new byte[data.length / 8];
		for(int ib = 0; ib < ba.length; ++ib){
			byte b = 0;
			for(int i = 0; i < 8; ++i){
				b <<= 1;
				if(data[ib * 8 + (7 - i)])
					b |= 1;
			}
			ba[ib] = b;
		}
		return ba;
	}

	public static boolean[] byteToBoolArray(byte[] byteArray){
		boolean[] boolArray = new boolean[byteArray.length * 8];
		for(int ib = 0; ib < byteArray.length; ++ib){
			for(int i = 0; i < 8; ++i){
				boolArray[ib * 8 + i] = (((byteArray[ib] >> i) & 1) == 1);
			}
		}
		return boolArray;
	}

	public void print(int pos, int length){
		for(int i = pos; i < pos + length; ++i){
			System.out.print(data[i] ? '1' : '0');
		}
		System.out.println();
	}

	public static void print(boolean[] data){
		for(int i = 0; i < data.length; ++i){
			System.out.print(data[i] ? '1' : '0');
		}
		System.out.println();
	}
}
