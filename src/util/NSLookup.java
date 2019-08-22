package util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

public class NSLookup {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		try {
			while (true) {
				System.out.print("> ");
				String url = sc.next();
				if (url.equals("exit")) {
					break;
				} else {
					InetAddress[] inetAddresses = InetAddress.getAllByName(url);
					for (InetAddress inetAddress : inetAddresses) {
						System.out.println(url + " : " + inetAddress.getHostAddress());
					}
				}
			}

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} finally {
			sc.close();
		}
	}
}
