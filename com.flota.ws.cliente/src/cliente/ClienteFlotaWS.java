package cliente;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class ClienteFlotaWS {

	// Sustituye esta clase por tu version de la clase Juego de la practica 1
	// Modificala para que instancie un objeto de la clase AuxiliarClienteFlota en el metodo 'ejecuta'
	// Modifica todas las llamadas al objeto de la clase Partida
	// por llamadas al objeto de la clase AuxiliarClienteFlota.
	// Los metodos a llamar tendran la misma signatura.

	public static final int NUMFILAS=8, NUMCOLUMNAS=8, NUMBARCOS=6;
	public static final int AGUA = -1, TOCADO = -2, HUNDIDO = -3;

	private GuiTablero guiTablero = null;			// El juego se encarga de crear y modificar la interfaz grafica // Objeto con los datos de la partida en juego
	private GestorPartidas gestor;

	/** Atributos de la partida guardados en el juego para simplificar su implementacion */
	private int quedan = NUMBARCOS, disparos = 0;
	/**
	 * Programa principal. Crea y lanza un nuevo juego
	 * @param args
	 */
	public static void main(String[] args) {
		ClienteFlotaWS cliente = new ClienteFlotaWS();
		cliente.ejecuta();
	} // end main

	/**
	 * Lanza una nueva hebra que crea la primera partida y dibuja la interfaz grafica: tablero
	 */
	private void ejecuta() {//cambiar new partida por aux cliente para poder acceder.
		// Instancia la primera partida

		gestor = new GestorPartidas();		//Creamos el objeto de la clase Auxiliar
		gestor.nuevaPartida(NUMFILAS, NUMCOLUMNAS, NUMBARCOS);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				guiTablero = new GuiTablero(NUMFILAS, NUMCOLUMNAS);
				guiTablero.dibujaTablero();
			}
		});
	} // end ejecuta

	/******************************************************************************************/
	/*********************  CLASE INTERNA GuiTablero   ****************************************/
	/******************************************************************************************/
	private class GuiTablero {

		private int numFilas, numColumnas;

		private JFrame frame = null;        // Tablero de juego
		private JLabel estado = null;       // Texto en el panel de estado
		private JButton buttons[][] = null; // Botones asociados a las casillas de la partida

		/**
		 * Constructor de una tablero dadas sus dimensiones
		 */
		GuiTablero(int numFilas, int numColumnas) {
			this.numFilas = numFilas;
			this.numColumnas = numColumnas;
			frame = new JFrame();
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	
		}

		/**
		 * Dibuja el tablero de juego y crea la partida inicial
		 */
		public void dibujaTablero() {
			anyadeMenu();
			anyadeGrid(numFilas, numColumnas);		
			anyadePanelEstado("Intentos: " + disparos + "    Barcos restantes: " + quedan);		
			frame.setSize(300, 300);
			frame.setVisible(true);	
		} // end dibujaTablero

		/**
		 * Anyade el menu de opciones del juego y le asocia un escuchador
		 */
		private void anyadeMenu() {
			// POR IMPLEMENTAR
			JMenu menu;
			JMenuBar barra;
			JMenuItem salir, nuevaPartida, solucion;
			barra=new JMenuBar();							//crea la barra superior donde se colocara el desplegable
			menu = new JMenu("Opciones");					//crea el menu desplegable
			salir=new JMenuItem("Salir");					
			nuevaPartida= new JMenuItem("Nueva Partida");	//crea los botones de dentro del desplegale
			solucion= new JMenuItem("Solucion");			
			menu.add(nuevaPartida);
			menu.add(solucion);								//añade los botones al desplegable
			menu.add(salir);  
			barra.add(menu);								//añade el desplegable a la barra superior
			frame.setJMenuBar(barra);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);

			solucion.addActionListener(new MenuListener());
			nuevaPartida.addActionListener(new MenuListener());			//escuchadores de los botones
			salir.addActionListener(new MenuListener());

		} // end anyadeMenu

		/**
		 * Anyade el panel con las casillas del mar y sus etiquetas.
		 * Cada casilla sera un boton con su correspondiente escuchador
		 * @param nf	numero de filas
		 * @param nc	numero de columnas
		 */
		private void anyadeGrid(int nf, int nc) {
			// POR IMPLEMENTAR
			ButtonListener listener = new ButtonListener();
			buttons=new JButton[nf][nc];
			JPanel panel = new JPanel();
			nf++;
			nc+=2;
			panel.setLayout(new GridLayout(nf, nc));
			char c = 'A';
			//dibuja la primera fila de la matriz * 1 2 3 4 5 6 7 8 *  
			for (int a = 0; a < nc ; a++) { 
				Integer ent = a;
				if(a == 0|| a== nc-1) panel.add(new JLabel("", JLabel.CENTER));
				else panel.add(new JLabel(ent.toString(), JLabel.CENTER));

			}
			//crea el resto de la matriz, botones y las letras de las coordenadas
			for(int i = 1; i<nf; i++) {	
				for (int j =0; j<nc ; j++) {
					if(j == 0 || j == nc-1) panel.add(new JLabel(Character.toString(c), JLabel.CENTER));
					else { 
						JButton boton = new JButton();
						boton.putClientProperty("fila", i-1);		//guardamos los datos de cada boton para usarlos en el escuchador
						boton.putClientProperty("col", j-1);
						boton.addActionListener(listener);
						buttons[i-1][j-1] = boton;				//añadimos los botones a la matriz de botones 
						panel.add(boton);
					}
				}
				c++;
			}


			frame.add(panel);

		} // end anyadeGrid


		/**
		 * Anyade el panel de estado al tablero
		 * @param cadena	cadena inicial del panel de estado
		 */
		private void anyadePanelEstado(String cadena) {	
			JPanel panelEstado = new JPanel();
			estado = new JLabel(cadena);
			panelEstado.add(estado);
			// El panel de estado queda en la posicion SOUTH del frame
			frame.getContentPane().add(panelEstado, BorderLayout.SOUTH);
		} // end anyadePanel Estado

		/**
		 * Cambia la cadena mostrada en el panel de estado
		 * @param cadenaEstado	nuevo estado
		 */
		public void cambiaEstado(String cadenaEstado) {
			estado.setText(cadenaEstado);
		} // end cambiaEstado

		/**
		 * Muestra la solucion de la partida y marca la partida como finalizada
		 */
		public void muestraSolucion() {
			for(int i=0;i<NUMFILAS;i++){
				for (int j = 0; j < NUMCOLUMNAS; j++) {
					//veremos que nos devuelve prueba casilla 
					//y en funcion de eso vamos haciendo
					int valor = gestor.pruebaCasilla(i, j);
					//segun el valor que nos devuelva pintaremos una cosa o otra
					//solo me interesan 2 de los 3 valores
					//el de tocado(que sera Rojo y el del agua que sera Azul
					//corregir esto
					if(valor==Partida.AGUA){
						pintaBoton(buttons[i][j], Color.CYAN);
					}else{
						pintaBoton(buttons[i][j], Color.RED);
					}
					quedan = 0;
					guiTablero.cambiaEstado("Game Over");
					

				}

			}
		} // end muestraSolucion


		/**
		 * Pinta un barco como hundido en el tablero
		 * @param cadenaBarco	cadena con los datos del barco codifificados como
		 *                      "filaInicial#columnaInicial#orientacion#tamanyo"
		 */
		public void pintaBarcoHundido(String cadenaBarco) {
			// POR IMPLEMENTAR
			String[] vectorBarco = cadenaBarco.split("#");
			String filaIni = vectorBarco[0];
			String colIni= vectorBarco[1];
			String orientacion= vectorBarco[2];
			String tamanyo= vectorBarco[3];
			int fila=Integer.parseInt(filaIni);
			int col = Integer.parseInt(colIni);
			if(Integer.parseInt(tamanyo)==1) {
				JButton boton = buttons[fila][col];
				guiTablero.pintaBoton(boton, Color.RED);
			}else {
				for(int i = 0; i< Integer.parseInt(tamanyo) ; i++) {
					if(orientacion.equals("H")) {
						JButton boton = buttons[fila][col];
						guiTablero.pintaBoton(boton, Color.RED);
						col++;
					}else if(orientacion.equals("V")){
						JButton boton = buttons[fila][col];
						guiTablero.pintaBoton(boton, Color.RED);
						fila++;
					}
				}
			}


		} // end pintaBarcoHundido

		/**
		 * Pinta un botÃ³n de un color dado
		 * @param b			boton a pintar
		 * @param color		color a usar
		 */
		public void pintaBoton(JButton b, Color color) {
			b.setBackground(color);
			// El siguiente cÃ³digo solo es necesario en Mac OS X
			b.setOpaque(true);
			b.setBorderPainted(false);
		} // end pintaBoton

		/**
		 * Limpia las casillas del tablero pintÃ¡ndolas del gris por defecto
		 */
		public void limpiaTablero() {
			for (int i = 0; i < numFilas; i++) {
				for (int j = 0; j < numColumnas; j++) {
					buttons[i][j].setBackground(null);
					buttons[i][j].setOpaque(true);
					buttons[i][j].setBorderPainted(true);
				}
			}
		} // end limpiaTablero

		/**
		 * 	Destruye y libera la memoria de todos los componentes del frame
		 */
		public void liberaRecursos() {
			frame.dispose();
		} // end liberaRecursos


	} // end class GuiTablero

	/******************************************************************************************/
	/*********************  CLASE INTERNA MenuListener ****************************************/
	/******************************************************************************************/

	/**
	 * Clase interna que escucha el menu de Opciones del tablero
	 * 
	 */
	private class MenuListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// POR IMPLEMENTAR																//Nuevo actionPerformed con implementación case
			String comando = e.getActionCommand();											//Los comandos a realizar al pulsar cada boton de las opciones
			switch(comando) {
			case "Nueva Partida":
				guiTablero.limpiaTablero();
				disparos = 0;
				quedan = NUMBARCOS;
				guiTablero.cambiaEstado("Intentos: " + disparos + "    Barcos restantes: " + quedan);
				gestor.nuevaPartida(NUMFILAS, NUMCOLUMNAS, NUMBARCOS);
				break;
			case "Solucion"://deberia de pillar el try pero no lo pilla
				guiTablero.muestraSolucion();
				break;
			case "Salir":
				System.exit(0);
				break;
			}
		} // end actionPerformed

	} // end class MenuListener



	/******************************************************************************************/
	/*********************  CLASE INTERNA ButtonListener **************************************/
	/******************************************************************************************/
	/**
	 * Clase interna que escucha cada uno de los botones del tablero
	 * Para poder identificar el boton que ha generado el evento se pueden usar las propiedades
	 * de los componentes, apoyandose en los metodos putClientProperty y getClientProperty
	 */
	private class ButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			JButton boton; 
			boton = (JButton) e.getSource();
			int fila = (int) boton.getClientProperty("fila");
			int col = (int) boton.getClientProperty("col");
			int estado = gestor.pruebaCasilla(fila, col);

			switch (estado){
			case AGUA: //Se ha tocado a agua
				if(!boton.getBackground().equals(Color.CYAN) && !boton.getBackground().equals( Color.ORANGE) &&  !boton.getBackground().equals(Color.RED) && quedan>0) 
					guiTablero.pintaBoton(boton , Color.CYAN);
				break;
			case TOCADO: //Se ha tocado un barco
				if(!boton.getBackground().equals(Color.CYAN) && !boton.getBackground().equals( Color.ORANGE) &&  !boton.getBackground().equals(Color.RED) && quedan>0) 
					guiTablero.pintaBoton(boton , Color.ORANGE);
				break;
			default: 
				if(estado>AGUA) { //Se ha tocado un barco y pasa a hundido
					quedan--;
					guiTablero.pintaBarcoHundido(gestor.getBarco(estado));
					if(quedan==0) { //Se llama cuando acaba la partida
						guiTablero.muestraSolucion();
					}
				}
				break;
			}
			disparos++;
			if(quedan == 0) guiTablero.cambiaEstado("Game Over"); 

			else guiTablero.cambiaEstado("Intentos: " + disparos + "    Barcos restantes: " + quedan);


		} // end actionPerformed

	} // end class ButtonListener


} // end class Juego
