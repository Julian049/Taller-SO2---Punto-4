README de compilación y ejecución

Requisitos:
- JDK instalado
- Maven instalado

1) Compilar y ejecutar con javac (Windows - CMD)
Estando en la carpeta RAIZ del proyecto

Creamos carpeta de salida: mkdir out

Compilamos las clases junto con las librerías de Maven
    javac -cp "lib/*;src\main\java" -d out src\main\java\co\edu\uptc\Bocanegra_Segura__Gonzalez_Diaz__mmu\*.java src\main\java\co\edu\uptc\Bocanegra_Segura__Gonzalez_Diaz__paginacion\*.java 


Con esto ya podemos ejecutar cada main de la siguiente forma:
    java -cp out co.edu.uptc.Bocanegra_Segura__Gonzalez_Diaz__mmu.Main
    java -cp "out;lib/*" co.edu.uptc.Bocanegra_Segura__Gonzalez_Diaz__paginacion.Main
