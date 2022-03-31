import java.util.ArrayList;
import java.util.Scanner;

public class QSimulator {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the number of qbits you want to use");
        int num = sc.nextInt();
        qcircuit cir = new qcircuit(num);
        String gate = "";
        while(true) {
            System.out.println("Enter the gate you would like to apply. If you want to start simulation, then type \"Simulate\" and enter");
            System.out.println("H for hadamard, X for puli-x, Y for pauli-y, Z for pauli-z, S for phase(space and enter the phase as well), CX for Controlled Not");
            System.out.println("Rx for rotation along x-axis, Ry for rotation along y-axis, Rz for rotation along z-axis");
            sc.nextLine();
            gate = sc.nextLine();
            if(gate.equals("Simulate") || gate.equals("s") || gate.equals("S")){
                break;
            }

            if(gate.equals("CX") || gate.equals("cx") ){
                System.out.println("Enter the control qubit (0 as 1st qubit)");
                int control = sc.nextInt();
                System.out.println("Enter the target qubit (0 as 1st qubit)");
                int target = sc.nextInt();

                if(cir.arrqbit[control] == null){
                    cir.arrqbit[control] = new qbit();
                }

                if(cir.arrqbit[target] == null){
                    cir.arrqbit[target] = new qbit();
                }

                ComplexNumber[] cnotted = tensorPro(cir.arrqbit[control].bits, cir.arrqbit[target].bits);

                // show that target qubit will not have any value anymore;

                ComplexNumber temp = cnotted[2];
                cnotted[2] = cnotted[3];
                cnotted[3] = temp;
                cir.arrqbit[target].inUse = false;
                cir.arrqbit[target].setBits(null);
                cir.arrqbit[control].setBits(cnotted);
                cir.arrqbit[control].addEntangled(target);

            }else {
                System.out.println("Enter the position of qubit you would like to apply to ( 0 to "+(num-1)+" )");
                int gateqbit = sc.nextInt();

                if(cir.arrqbit[gateqbit] == null){
                    cir.arrqbit[gateqbit] = new qbit();
                }

                if(gate.equals("H") || gate.equals("h")){
                    cir.arrqbit[gateqbit].bits = cir.arrqbit[gateqbit].hadamard();
                }else if (gate.equals("X") || gate.equals("x")){
                    cir.arrqbit[gateqbit].bits = cir.arrqbit[gateqbit].paulix();
                }else if(gate.equals("Y") || gate.equals("y")){
                    cir.arrqbit[gateqbit].bits = cir.arrqbit[gateqbit].pauliy();
                }else if(gate.equals("Z") || gate.equals("z")){
                    cir.arrqbit[gateqbit].bits = cir.arrqbit[gateqbit].pauliz();
                }else if(gate.charAt(0) == 'R'){
                    System.out.println("Enter the angle (in radians)");
                    double rad = sc.nextDouble();
                    if(gate.equals("Rx") || gate.equals("RX") || gate.equals("rx")){
                        cir.arrqbit[gateqbit].bits = cir.arrqbit[gateqbit].rx(rad);
                    }else if (gate.equals("Ry") || gate.equals("RY") || gate.equals("ry")){
                        cir.arrqbit[gateqbit].bits = cir.arrqbit[gateqbit].ry(rad);
                    }else if (gate.equals("Rz") || gate.equals("RZ") || gate.equals("rz")) {
                        cir.arrqbit[gateqbit].bits = cir.arrqbit[gateqbit].rz(rad);
                    }
                }else{
                    System.out.println("Please enter the correct gate");
                }
            }
        }

        System.out.println("************** Debugging **************");

        for(int i =0; i<cir.arrqbit.length; i++) {
            if(cir.arrqbit[i] == null){
                cir.arrqbit[i] = new qbit();
            }
            System.out.println("Qbit: "+i);
            if(cir.arrqbit[i].bits != null) {

                System.out.println("cir.arrqbit[i].bits length = " + cir.arrqbit[i].bits.length);

                for (ComplexNumber var : cir.arrqbit[i].bits) {
                    if (var != null) {
                        System.out.println(var);
                    }
                }
            }else{
                System.out.println("cir.arrqbit[i].bits is null");
            }
        }
        ComplexNumber[] res = tens(cir);

        System.out.println();
        System.out.println("************************************");
        System.out.println("************** Result **************");
        System.out.println("************************************");
        System.out.println();

        String[] binaryValues = binarySeq(num);


        for(int i =0; i< res.length;i++){
            System.out.print(res[i]);
            double per = (res[i].real*res[i].real + res[i].img * res[i].img) * 10000;

            double p = Math.round(per)/100;

            System.out.println( "  | "+binaryValues[i] + " > , Probability = " + p + " %");
        }
    }

    public static ComplexNumber[][] tensorPro(ComplexNumber[][] m1,ComplexNumber[][] m2){
        ComplexNumber[][] res = new ComplexNumber[m1.length * m2.length][m1[0].length * m2[0].length];

        for(int i=0;i<m1.length;i++){
            for(int x=0;x< m2.length;x++){
                for(int j=0; j <m1[0].length;j++){
                    for(int y=0;y<m2[0].length;y++){
                        res[i + y + 1][j + x + 1] = m1[i][j].multiply(m2[x][y]);
                    }
                }
            }
        }

        return res;

    }

    public static ComplexNumber[][] tensorPro(ComplexNumber[] v1,ComplexNumber[][] m1){
        ComplexNumber[][] res = new ComplexNumber[v1.length*m1.length][m1[0].length];

        for(int i=0; i<v1.length; i++){
            for(int j=0;j<m1.length;j++){
                for (ComplexNumber var :m1[j]){
                    res[i][j] = var.multiply(v1[i]);
                }
            }
        }

        return res;

    }

    public static ComplexNumber[] tensorPro(ComplexNumber[] v1, ComplexNumber[] v2){

        ComplexNumber[] res = new ComplexNumber[v1.length * v2.length];
        int count =0;
        for (ComplexNumber i : v1){
            for( ComplexNumber j : v2){
                res[count] = i.multiply(j);
                count++;
            }
        }

        return res;
    }

    public static ComplexNumber[] tens(qcircuit cir){

        qbit[] qubitArr = getVec(cir);

        ComplexNumber[] res = qubitArr[0].bits;=
        for ( int i=1; i < qubitArr.length; i ++){
            res = tensorPro(res,qubitArr[i].bits);
        }

        return res;

    }

    public static qbit[] getVec(qcircuit cir){

        ArrayList<qbit> arrlis = new ArrayList<qbit>();
        int c = 0;
        for ( qbit var : cir.arrqbit ) {
            if(var.inUse){
                arrlis.add(var);
            }
        }
        qbit[] arr = new qbit[arrlis.size()];
        arr = arrlis.toArray(arr);
        return arr;
    }

    public static ComplexNumber[][] matrixMultiplication(ComplexNumber[][] m1, ComplexNumber[][] m2){
        if(m1[0].length == m2.length){

            ComplexNumber[][] res = new ComplexNumber[m1.length][m2[0].length];

            for(int x=0;x < m1.length; x++){
                for(int y=0;y<m2[0].length;y++){
                    for(int z=0;z<m1[0].length;z++){
                        res[x][y] = res[x][y].add(m1[x][z].multiply(m2[z][y]));
                    }
                }
            }

            return res;

        }else{
            System.out.println("Matrix multiplication invalid");
            return null;
        }
    }

    public static String[] binarySeq(int n){
        String[] arr = new String[(int)Math.pow(2,n)];
        for(int i =0;i <Math.pow(2,n); i++){
            arr[i] = Integer.toBinaryString(i);
            while(arr[i].length() < n){
                arr[i] = "0" + arr[i];
            }
        }
        return arr;
    }

}

class qcircuit{
    qbit[] arrqbit;

    qcircuit(int num){
        arrqbit = new qbit[num];
    }
}

class qbit {
    ComplexNumber[] bits;
    boolean inUse = true;
    ArrayList<Integer> entangled;

    qbit(){
        bits = new ComplexNumber[2];
        bits[0] = new ComplexNumber(1,0);
        bits[1] = new ComplexNumber(0,0);
    }

    qbit(int b){
        bits = new ComplexNumber[b];
        for (ComplexNumber c: bits) {
            c = new ComplexNumber(0,0);
        }
    }

    public void addEntangled(int val){
        if(entangled == null){
            entangled = new ArrayList<Integer>(10);
        }
        entangled.add(val);
    }

    public void setInUse(boolean value){
        inUse = value;
    }

    // change the size of vector from 2 to any required number
    public void changeSize(int val){
        bits = new ComplexNumber[val];
    }


    public void setBits(ComplexNumber[] val){
        bits = val;
    }

    public ComplexNumber[] hadamard(){
        ComplexNumber[][] matrix = {{new ComplexNumber(1/Math.sqrt(2),0),new ComplexNumber(1/Math.sqrt(2),0)},{new ComplexNumber(1/Math.sqrt(2),0),new ComplexNumber(1/Math.sqrt(2),0)}};
        return matrixMulti(matrix);
    }

    public ComplexNumber[] paulix() {

        ComplexNumber[][] matrix = {{new ComplexNumber(0,0),new ComplexNumber(1,0)},{new ComplexNumber(1,0),new ComplexNumber(0,0)}};
        return matrixMulti(matrix);
    }

    public ComplexNumber[] pauliy(){
        ComplexNumber[][] matrix = {{new ComplexNumber(0,0),new ComplexNumber(0,-1)},{new ComplexNumber(0,1),new ComplexNumber(0,0)}};
        return matrixMulti(matrix);
    }

    public ComplexNumber[] pauliz(){
        ComplexNumber[][] matrix = {{new ComplexNumber(1,0),new ComplexNumber(0,0)},{new ComplexNumber(0,0),new ComplexNumber(-1,0)}};
        return matrixMulti(matrix);
    }

    public ComplexNumber[] rx(double rad){
        ComplexNumber[][] matrix = {{new ComplexNumber(Math.cos(rad/2),0),new ComplexNumber(0,-Math.sin(rad/2))},{new ComplexNumber(0,-Math.sin(rad/2)),new ComplexNumber(Math.cos(rad/2),0)}};
        return matrixMulti(matrix);
    }

    public ComplexNumber[] ry(double rad){
        ComplexNumber[][] matrix = {{new ComplexNumber(Math.cos(rad/2),0),new ComplexNumber(-Math.sin(rad/2),0)},{new ComplexNumber(Math.sin(rad/2),0),new ComplexNumber(Math.cos(rad/2),0)}};
        return matrixMulti(matrix);
    }

    public ComplexNumber[] rz(double rad){
        ComplexNumber[][] matrix = {{new ComplexNumber(0,Math.exp(-rad/2)),new ComplexNumber(0,0)},{new ComplexNumber(0,0),new ComplexNumber(0,Math.exp(rad/2))}};
        return matrixMulti(matrix);
    }

    public ComplexNumber[] matrixMulti(ComplexNumber[][] matrix){

        if(matrix.length == 2 && matrix[0].length==2) {
            ComplexNumber[] cn = new ComplexNumber[2];
            cn[0] = bits[0].add(this.bits[0].multiply(matrix[0][0]),this.bits[1].multiply(matrix[0][1]));
            cn[1] = bits[0].add(this.bits[0].multiply(matrix[1][0]),this.bits[1].multiply(matrix[1][1]));
            return cn;
        }else{
            System.out.println("Please, Enter a 2x2 matrix");
            return this.bits;
        }
    }
}

class ComplexNumber{
    double real;
    double img;

    ComplexNumber(double r, double im){
        real = r;
        img = im;
    }

    public ComplexNumber add(ComplexNumber n1, ComplexNumber n2){
        return new ComplexNumber(n1.real+n2.real, n1.img+n2.img);
    }

    public ComplexNumber multiply(ComplexNumber n1){
        return new ComplexNumber(((this.real*n1.real) - (this.img * n1.img)), ((this.real*n1.img) + (img*n1.real)));
    }

    public String toString()
    {
        double r = Math.round(real*100);
        double i = Math.round(img*100);
        return "( "+(r/100)+", "+i+" i )";
    }

    public ComplexNumber subtract(ComplexNumber n1){
        return new ComplexNumber(this.real - n1.real, this.img - n1.img);
    }

    public ComplexNumber subtract(double n1){

        return new ComplexNumber(this.real -n1, this.img);
    }

    public ComplexNumber multiply(double n1){
        return new ComplexNumber(this.real * n1,this.img *n1);
    }

    public ComplexNumber multiply(ComplexNumber n1, ComplexNumber n2) {
        return new ComplexNumber(n1.real * n2.real - n1.img * n2.img, n1.img * n2.real + n1.real * n2.img);
    }

    public ComplexNumber add(ComplexNumber n1){
        return  new ComplexNumber(this.real + n1.real,this.img + n1.img );
    }

    public ComplexNumber add(double n1){
        return new ComplexNumber(this.real + n1,this.img);
    }
}
