package problem;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.google.gson.Gson;

public class MyClientController {

	private ProductSystemUI v;
	private Gson gson;
	private BufferedReader br;
	private PrintWriter pw;
	private HTTPRequestController rc;

//	private String serverURL="203.252.148.148";
	private String serverURL="172.21.192.1";
	private int port = 80;
	//private String serverURL = "localhost";

	public MyClientController(ProductSystemUI v) {
		this.v = v;
		this.gson = new Gson();
		this.rc = new HTTPRequestController(serverURL);
	}

	public void login() {
		Socket socket = null;
		try {
			socket = new Socket(serverURL, port);
			br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			pw = new PrintWriter(socket.getOutputStream());

			// request header 세팅
			rc.setLoginRequest(pw, gson.toJson(new User(v.idInput.getText())));

			socket.shutdownOutput();

			v.model.setRowCount(0);
			String line = null;
			StringBuilder s = new StringBuilder();

			while ((line = br.readLine()) != null) {
				s.append(line + "\r\n");
			}

			String response = s.toString();

			// 올바른 Response일 때만 로그인 이벤트 처리
			if (response.indexOf("HTTP/") != -1) {
				if (response.indexOf("200 OK") != -1) {
					String tempId = v.idInput.getText();
					v.cardLayout.show(v.tab, "main");
					v.loginInfo.setText(tempId);
					getProducts();
				}
			}

		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();

				if (pw != null)
					pw.close();

				if (socket != null)
					socket.close();

			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	public void getProducts() {
		Socket socket = null;
		try {
			socket = new Socket(serverURL, port);

			br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			pw = new PrintWriter(socket.getOutputStream());

			// request header 세팅
			rc.setGetRequest(pw, v.idInput.getText());
			socket.shutdownOutput();

			// TODO : Response 내용을 받아오는 코드를 작성하시오
			String line = null;
			StringBuilder s = new StringBuilder();

			while ((line = br.readLine()) != null) {
				s.append(line + "\r\n");
			}

			String response = s.toString();


			// TODO : 올바른 Response 일 때 상품 조회 이벤트를 처리하시오
			// 참고 : ResponseBody 받아오는 방법 -> Response에서 "\r\n\r\n"를 구분자로 split하여 받아옴

			if (response.indexOf("HTTP/") != -1) {
				if (response.indexOf("200 OK") != -1) {
					System.out.println(response);
					String responseBody = null;
					try{
						responseBody = response.split("\r\n\r\n")[1];
					}catch(ArrayIndexOutOfBoundsException e){
						System.out.println("response body is empty");
					}
					if(responseBody != null){
						String[] productJsons = responseBody.split("\n");
						Product[] products = new Product[productJsons.length];
						for(int i = 0; i < productJsons.length; i++){
							products[i] = gson.fromJson(productJsons[i], Product.class);
						}
						v.model.setNumRows(0);
						for(int i = 0; i < productJsons.length; i++){
							v.model.addRow(new Object[]{products[i].getOrderId(), products[i].getName(), products[i].getStatus(), products[i].getCreatedAt()});
						}
					}else{
						v.model.setNumRows(0);
					}
				}
			}



		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
				if (pw != null)
					pw.close();

				if (socket != null)
					socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	public void addProducts() {
		Socket socket = null;
		try {
			socket = new Socket(serverURL, port);

			br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			pw = new PrintWriter(socket.getOutputStream());

			//request header 세팅
			rc.setPostRequest(pw, gson.toJson(new Product(v.idInput.getText(), v.nameInput.getText())));
			socket.shutdownOutput();

			
			// TODO : Response 내용을 받아오는 코드를 작성하시오
			String line = null;
			StringBuilder s = new StringBuilder();

			while ((line = br.readLine()) != null) {
				s.append(line + "\r\n");
			}

			String response = s.toString();

			// TODO : 올바른 Response일 때 상품을 새로고침 하시오
			if (response.indexOf("HTTP/") != -1) {
				if (response.indexOf("200 OK") != -1) {
					getProducts();
				}
			}

		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();

				if (pw != null)
					pw.close();

				if (socket != null)
					socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	public void updateProduct() {
		Socket socket = null;
		try {
			socket = new Socket(serverURL, port);

			br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			pw = new PrintWriter(socket.getOutputStream());

			//request header 설정 (PUT이 안될 경우 PATCH 사용)
			rc.setPatchRequest(pw, gson.toJson(
					new Product(Long.parseLong(v.txt1.getText().toString()), v.txt2.getText(), v.txt3.getText())));
			// rc.setPutRequest(pw, gson.toJson(
			// new Product(Long.parseLong(v.txt1.getText().toString()), v.txt2.getText(),v.txt3.getText())));
			
			socket.shutdownOutput();

			// TODO : Response 내용을 받아오는 코드를 작성하시오
			String line = null;
			StringBuilder s = new StringBuilder();

			while ((line = br.readLine()) != null) {
				s.append(line + "\r\n");
			}

			String response = s.toString();



			// TODO : 올바른 Response일 때 상품을 새로고침 하는 코드를 작성하시오 
			if (response.indexOf("HTTP/") != -1) {
				if (response.indexOf("200 OK") != -1) {
					getProducts();
				}
			}

		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();

				if (pw != null)
					pw.close();

				if (socket != null)
					socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	public void deleteProduct() {
		Socket socket = null;
		try {
			socket = new Socket(serverURL, port);

			br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			pw = new PrintWriter(socket.getOutputStream());

			//request header 설정
			rc.setDeleteRequest(pw, v.txt1.getText());
			socket.shutdownOutput();

			// TODO : Response 내용을 받아오는 코드를 작성하시오
			String line = null;
			StringBuilder s = new StringBuilder();

			while ((line = br.readLine()) != null) {
				s.append(line + "\r\n");
			}

			String response = s.toString();

			// TODO : 올바른 Response일 때 상품을 새로고침 하는 코드를 작성하시오
			if (response.indexOf("HTTP/") != -1) {
				if (response.indexOf("200 OK") != -1) {
					getProducts();
				}
			}

		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();

				if (pw != null)
					pw.close();

				if (socket != null)
					socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	public void appMain() {
		v.addButtonActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Object obj = e.getSource();
				if (obj == v.loginButton) {
					//로그인
					login();
				} else if (obj == v.exitButton) {
					System.exit(0);
				} else if (obj == v.addButton) {
					// 상품 추가
					addProducts();
					v.nameInput.setText("");
				} else if (obj == v.updateButton) {
					// 상품 업데이트
					updateProduct();
					//TODO : STEP1 이벤트 추가하기
					v.txt1.setText("");
					v.txt2.setText("");
					v.txt3.setText("");
					v.txt4.setText("");
				} else if (obj == v.deleteButton) {
					// 상품 삭제
					deleteProduct();
					
					//TODO : STEP1 이벤트 추가하기
					v.txt1.setText("");
					v.txt2.setText("");
					v.txt3.setText("");
					v.txt4.setText("");
				}
			}
		});
	}

	public static void main(String[] args) {
		MyClientController mc = new MyClientController(new ProductSystemUI());
		mc.appMain();
	}
}
