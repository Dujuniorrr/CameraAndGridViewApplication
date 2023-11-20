# Capturando Imagens e Listando com GridView

Este repositório contém uma aplicação simples que utiliza a câmera do celular para tirar fotos. Em seguida, as fotos são listadas em uma GridView. O objetivo é fornecer uma demonstração didática das funcionalidades mencionadas para os alunos da disciplina de Laboratório de Programação para Dispositivos Móveis, facilitando a aprendizagem e a aplicação prática desses conceitos.

## Sumário
1. [Capturando imagens pela camêra](#cam)
2. [Carregando imagens da galeria](#galery)
3. [Salvando imagens](#save)
4. [Criando GridView](#gridview)
5. [Utilizando GridView](#put-images)

## Permissões

Para retirar fotos, acessar a galeria e listar imagens em seu app, é necessário modificar o arquivo `AndroidManifest.xml` com as seguintes permissôes:

```xml

 <uses-feature
        android:name="android.hardware.camera"
        android:required="true" />
    <uses-permission android:name="android.permission.INTERNET"/>
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.MANAGE_EXTERNAL_STORAGE"
        tools:ignore="ScopedStorage" />


```

<a id="cam"></a>
## 1. Capturando imagens pela camêra


Em seu layout crie um botão para iniciar o evento de abertura da camêra, na `MainActivity.java` instancie esse botão, bem como um identificador para a resposta da camêra.

<br>

```java
    private static final int pic_id = 123;
    MaterialButton addImg;
```
<br>

Para capturar imagens com a camêra, é necessário criar uma Intent para abrir a câmera do dispositivo usando a ação `MediaStore.ACTION_IMAGE_CAPTURE`. Essa intenção é usada para solicitar à câmera que tire uma foto. Em seguida, inicie a atividade da câmera utilizando `startActivityForResult(camera_intent, pic_id)`. O segundo parâmetro `pic_id` é o código de solicitação que será usado para identificar a resposta desta atividade.

<br>

```java
  addImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent camera_intent
                        = new Intent(MediaStore
                        .ACTION_IMAGE_CAPTURE);

                startActivityForResult(camera_intent, pic_id);

            }
        });
```

<br>

O método onActivityResult é um callback no ciclo de vida de uma atividade Android que é chamado automaticamente quando uma atividade que foi iniciada com startActivityForResult é concluída. Ele recebe três parâmetros:

   * `requestCode`: Um código que identifica a origem da solicitação. Este código é usado para associar a resposta da atividade correta quando houver múltiplas atividades sendo iniciadas.

   * `resultCode`: Um código que indica o resultado da atividade. Geralmente, RESULT_OK é retornado se a atividade foi concluída com sucesso.

   * `data`: Um objeto Intent que pode conter dados adicionais devolvidos pela atividade. No contexto da captura de imagens, esse objeto Intent geralmente contém a imagem capturada.

A função foi desenvolvida da seguinte forma:
     
* `super.onActivityResult(requestCode, resultCode, data);`: Chama a implementação do método na superclasse, garantindo que o comportamento padrão seja executado.

 * `if (requestCode == pic_id)`: Verifica se o código de solicitação é igual ao pic_id, que é uma constante definida no início da classe.

 * `Bitmap photo = (Bitmap) data.getExtras().get("data");`: Obtém a imagem capturada da câmera do objeto Intent. No caso da captura de imagem pela câmera, a imagem é frequentemente incluída como um extra com a chave "data".

 * `saveImg(photo);`: Chama o método saveImg e passa a imagem capturada como parâmetro. Esse método contém a lógica para armazenar a imagem no dispositivo.

<br>

```java
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == pic_id) {
            Bitmap photo = (Bitmap) data.getExtras()
                    .get("data");
            saveImg(photo);
        }
    }
```

<br>

<a id="galery"></a>

## 2. Carregando imagens da galeria

Para adicionar uma imagem provinda da galeria do dispositivo, é necessário criar um botão no layout que irá ativar essa função ao capturar um evento de click.

```java
    MaterialButton addGalery;
```

Quando o botão addGalery é clicado, ele inicia uma atividade que permite ao usuário escolher uma imagem da galeria. O resultado dessa atividade (a URI da imagem escolhida) é tratado pelo ActivityResultLauncher registrado anteriormente no código. Esse padrão de uso de contratos de resultado de atividade simplifica a obtenção de resultados de atividades sem a necessidade de implementar manualmente o método onActivityResult.

 * `Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);`:
        Cria uma nova intenção `(Intent)` com a ação `Intent.ACTION_PICK`, indicando que a intenção deve ser usada para selecionar algo. Neste caso, a intenção é usada para selecionar um item da galeria de imagens.
        O segundo parâmetro, `MediaStore.Images.Media.EXTERNAL_CONTENT_URI`, especifica o tipo de dados a ser selecionado (imagens) e o local (armazenamento externo).

* `launcher.launch(intent);`:
        Utiliza o objeto launcher (que é um `ActivityResultLauncher`) para lançar a atividade representada pela intenção.
        O sistema de contratos de resultado de atividade cuida do restante do ciclo de vida da atividade (resultados e chamadas `onActivityResult`).

<br>

```java
   addGalery.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                   launcher.launch(intent);
               }
           });

```
<br>

O objeto launcher deve ser criado como um atributo da classe `MainActivy`, e instanciado da seguinte forma:

* `private final ActivityResultLauncher<Intent> launcher = registerForActivityResult(...)`:
        Isso cria uma instância de `ActivityResultLauncher` chamada launcher usando `registerForActivityResult`.
        `ActivityResultContracts.StartActivityForResult()` especifica que estamos interessados nos resultados de uma atividade que é iniciada para obter um resultado.
        O código dentro do lambda (função anônima) `(result -> { ... })` define o que fazer com o resultado da atividade.

* `if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {`:
        Verifica se o resultado da atividade foi bem-sucedido (RESULT_OK) e se há dados retornados.

* `Uri photoUri = result.getData().getData();`:
        Obtém a `URI` da foto a partir dos dados retornados pela atividade. Essa `URI` geralmente representa a localização da imagem na galeria ou em outro provedor de conteúdo.

* `try (InputStream inputStream = getContentResolver().openInputStream(photoUri)) {`:
        Usa o `ContentResolver` para obter um InputStream a partir da `URI` da foto. Isso é feito para que a imagem possa ser lida como bytes.

* `Bitmap bitmap = BitmapFactory.decodeStream(inputStream);`:
        Converte o InputStream em um objeto `Bitmap` usando a classe `BitmapFactory`. Isso transforma os bytes da imagem em uma representação que pode ser usada em um aplicativo Android.

* `saveImg(bitmap);`:
        Chama a função `saveImg`, passando o `Bitmap` como parâmetro. Esta função contém a lógica para salvar a imagem no armazenamento do dispositivo.

* `} catch (IOException e) { e.printStackTrace(); }`:
        Trata exceções caso ocorram durante a leitura da imagem. Neste caso, apenas imprime o rastreamento da pilha no console.

<br>

```java
 private final ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK
                        && result.getData() != null) {
                    Uri photoUri =  result.getData().getData();
                    try (InputStream inputStream = getContentResolver().openInputStream(photoUri)) {
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//                        clickImg.setImageBitmap(bitmap);// colocar a imagem da galeria na tela
                        saveImg(bitmap);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
    );

```

<br>

<a id="save"></a>

## 3. Salvando imagens

O método `saveImg` tem a responsabilidade de salvar uma imagem no armazenamento do dispositivo e atualizar a interface do usuário (a `GridView`) para refletir a adição da nova imagem. Vamos analisar cada parte do código:

* `String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, UUID.randomUUID().toString(), "");`:
        Utiliza a classe `MediaStore.Images.Media` para inserir a imagem no armazenamento do dispositivo.
        `getContentResolver()` é usado para obter o resolvedor de conteúdo associado à sua atividade.
        bitmap é o objeto Bitmap da imagem a ser salva.
        `UUID.randomUUID().toString()` gera um nome único para a imagem, evitando conflitos com nomes existentes.
        A string vazia "" é usada para a descrição da imagem.

* `if (path != null)`:
        Verifica se o caminho (path) retornado pela inserção não é nulo, o que indicaria que a imagem foi salva com sucesso.

* `Uri photoUri = Uri.parse(path);`:
        Converte o caminho (representado como uma string) em uma Uri. A Uri é um identificador uniforme de recursos que pode ser usado para acessar a imagem no armazenamento.

* `Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);`:
        Cria uma nova intenção (Intent) com a ação `Intent.ACTION_MEDIA_SCANNER_SCAN_FILE`.
        Essa ação instrui o `MediaScanner` a escanear o arquivo indicado pela Uri para que ele seja detectado pelo sistema de mídia (como a galeria de fotos).

* `mediaScanIntent.setData(photoUri);`:
        Define a Uri do arquivo a ser escaneado pela intenção.

* `sendBroadcast(mediaScanIntent);`:
        Envolve a intenção em um broadcast para notificar o sistema de que um novo arquivo foi adicionado, solicitando que o MediaScanner o inclua no banco de dados de mídia.

* `gridView.setAdapter(new CustomAdapter(this, getImagesFromMediaStore(getApplicationContext())));`:
        Atualiza a `GridView` utilizando um novo CustomAdapter que reflete as imagens presentes no MediaStore após a adição da nova imagem.

* `} else { Log.e(TAG, "Erro ao salvar a imagem na galeria"); }`:
        Se o caminho (path) for nulo, registra um erro no log indicando que houve um problema ao salvar a imagem na galeria.

Portanto, o método saveImg realiza o processo completo de salvar uma imagem no armazenamento, garantindo que ela seja reconhecida pelo sistema e, em seguida, atualiza a interface do usuário para refletir essa adição.

<br>

```java

    protected void saveImg(Bitmap bitmap){

        String path = MediaStore.Images.Media.insertImage(
                getContentResolver(),
                bitmap,
                UUID.randomUUID().toString(),
                ""
        );

        if (path != null) {
            Uri photoUri = Uri.parse(path);

            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(photoUri);
            sendBroadcast(mediaScanIntent);

            //recarrega o grid com nova imagem salva
            gridView.setAdapter(new CustomAdapter(this, getImagesFromMediaStore(getApplicationContext())));

        } else {
            Log.e(TAG, "Erro ao salvar a imagem na galeria");
        }
    }

```

<br>

<a id="gridview"></a>

## 4. Criando GridView


Para criar o GridView é preciso criar no diretório `Layout` um arquivo XML( ex: `activity_main.xml`), e colocar dentro do layout(`activity_main.xml`) um elemento do tipo GridView. O GridView serve para organiza itens. Seguindo uma organização tabular, contendo um número específico de colunas. Assim os itens são distribuídos automaticamente para preencher as linhas e colunas disponíveis, sendo quê, o GridView já  possui suporte integrado para rolagem de tela quando o número de itens é maior do que pode ser exibido em uma única tela. Para que essa organização ocorra, é necessário utilizar alguns atributos XML. 

* `android:id="@+id/GridView" `:
        Define um identificador único para o GridView. Esse id `GridView` é utilizado para referenciar o GridView.

* `android:layout_margin="10dp"`:
        Define uma margem de `10dp` em todos os lados do GridView. A margem é o espaço em branco ao redor do GridView.

* `android:horizontalSpacing="15dp"`:
        Define um espaçamento horizontal de `15dp` entre os itens do GridView. 

* `android:verticalSpacing="15dp"`:
        Define um espaçamento vertical de `15dp` entre os itens do GridView.

* `android:layout_width="match_parent"`:
        Define a largura do GridView para preencher completamente a largura do contêiner pai.

* `android:layout_height="match_parent"`:
        Define a altura do GridView para preencher completamente a altura do contêiner pai.

* `android:numColumns="3"`:
        Define o número de colunas no GridView como `3`. Isso significa que haverá `3` itens em cada linha. O GridView organizará automaticamente os itens em colunas e linhas com base nesse número.

<br>

```xml

 <GridView
        android:id="@+id/GridView"
        android:layout_margin="10dp"
        android:horizontalSpacing="15dp"
        android:verticalSpacing="15dp"
        android:layout_below="@id/btnImage"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:numColumns="3"/>
    
```
<br>

<a id="put-images"></a>
## 5. Utilizando GridView

Para utilizar o GridView é necessário criar dois arquivos no diretório `Layout`. O primeiro é o arquivo XML(`activity_main.xml`) contendo o elemento GridView e logo após, é preciso criar um outro arquivo XML( ex: `photos_list.xml` ) para descrever como cada item deve ser exibido. Este arquivo contém o layout de cada item individual que será apresentado dentro do GridView. Neste caso, precisamos apresentar imagens, então o arquivo secundário( `photos_list.xml` ) deve conter a representação de elemento do tipo ImageView.    
<br>

* `android:id="@+id/idImage`:
        Define um identificador único para a ImageView. Esse ID pode ser usado para referenciar a ImageView em código Java.

* `android:layout_width="100dp" e android:layout_height="100dp"`:
        Define a largura e altura da `ImageView` como `100dp`. Isso significa que a `ImageView` terá uma largura e altura fixas de `100dp`.

* `android:layout_gravity="center"`:
        Define a gravidade do layout da `ImageView` como `center`, o que significa que a `ImageView` será centralizada dentro do contêiner que a contém.

* `android:src="@mipmap/ic_launcher"`:
        Define a fonte da imagem exibida na `ImageView`. Neste caso, a imagem vem do recurso mipmap chamado `ic_launcher`. 

<br>

```xml

 <ImageView
            android:id="@+id/idImage"
            android:layout_width="100dp"
            android:layout_height="100dp"
            android:layout_gravity="center"
            android:src="@mipmap/ic_launcher"
         />
    
```
<br>

Para que o GridView e layout custumizado seja utilizados em conjunto, é preciso criar um adaptador. Ou seja, para apresentar dados em um GridView no Android, é necessário utilizar um Adapter. O Adapter é responsável por fornecer os dados ao GridView e também por criar as visualizações individuais (células) que serão exibidas na grade. O uso de um Custom Adapter é comum quando é preciso personalizar a aparência ou o comportamento das células do GridView. 
<br>
Custom Adapter é caraterizado como um arquivo java(ex: `CustomAdapter.java`) . Para boas práticas, é interessante criar uma diretório(ex: `adapter`) e colocar o arquivo.
<br>

* `public class CustomAdapter extends BaseAdapter {`:
        A classe `CustomAdapter` herda da classe `BaseAdapter`, que contem métodos e comportamentos específicos para a criação de adaptadores de lista personalizados. Isso permite utilizar métodos específicos na classe CustomAdapter para personalizar o comportamento conforme necessário.

* Variáveis de Instância:
  
   - `Context context`: Armazena o contexto da aplicação Android, fornecendo acesso a recursos e informações globais do aplicativo.
  
   - `ArrayList<String> imagePaths`: Mantém uma lista de caminhos das imagens que serão exibidas.

* Construtor:
   - `public CustomAdapter(Context context, ArrayList<String> imagePaths)`: O construtor da classe. Inicializa as variáveis de instância com os valores passados como parâmetros.

* Métodos de Adapter:
  
   -  `getCount()`: Retorna o número de itens na lista, que é o tamanho da lista de caminhos de imagens.
     
   - `getItem(int position)`: Retorna o item na posição especificada da lista de caminhos de imagens.
     
   - `getItemId(int position)`: Retorna o ID do item na posição especificada. Neste caso, é a própria posição.

* Método `getView()`:
        Este método é responsável por criar ou reutilizar a View para cada item na lista e configurá-la com os dados apropriados.
        Utiliza um layout inflado a partir do recurso `R.layout.photos_list`.
        Carrega a imagem associada à posição atual na `ImageView` usando o caminho da imagem armazenado em imagePaths. A imagem é carregada de forma assíncrona.
        Verifica se a `convertView` é nula. Se for, cria uma nova View; caso contrário, reutiliza a `convertView`.

* Método `getImageUri()`:
        Este método obtém a `URI` de uma imagem com base no caminho fornecido usando uma consulta ao provedor de conteúdo do Android ( `MediaStore` ).
        Usa o caminho da imagem para encontrar o ID correspondente no banco de dados de mídia.

```java


  public class CustomAdapter extends BaseAdapter {

    private final Context context;
    private final ArrayList<String> imagePaths;

    public CustomAdapter(Context context, ArrayList<String> imagePaths) {
        this.context = context;
        this.imagePaths = imagePaths;
    }

    @Override
    public int getCount() {
        return imagePaths.size();
    }

    @Override
    public Object getItem(int position) {
        return imagePaths.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View gridView;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            gridView = new View(context);
            gridView = inflater.inflate(R.layout.photos_list, null);
            ImageView imageView = gridView.findViewById(R.id.idImage);

            final InputStream imageStream;
            try {
                imageStream = context.getContentResolver().openInputStream(Objects.requireNonNull(
                getImageUri(context, imagePaths.get(position))));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            final Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
            imageView.setImageBitmap(bitmap);
//            Toast.makeText(context, bitmap.toString(), Toast.LENGTH_SHORT).show();
        } else {
            gridView = convertView;
        }

        return gridView;
    }

    public static Uri getImageUri(Context context, String imagePath) {
        String[] projection = {MediaStore.Images.Media._ID};
        String selection = MediaStore.Images.Media.DATA + " = ?";
        String[] selectionArgs = new String[]{imagePath};

        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            int idColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            long id = cursor.getLong(idColumnIndex);
            cursor.close();
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, Long.toString(id));
        }

        return null;
    }
}

```

<br>


Após a criação dos arquivos, será necesário pegar o caminho das imagens da memória externa. No arquivo `MainActivity.java` é preciso criar uma função para pegar o caminho das imagens.


   * `private ArrayList<String> getImagesFromMediaStore(Context context) {`:
      Essa parte do código define uma função chamada `getImagesFromMediaStore` que tem como objetivo obter os caminhos dos arquivos de imagem armazenados no dispositivo Android utilizando o provedor de conteúdo `MediaStore`.
     
   * `ArrayList<String> imagePaths = new ArrayList<>();`:
      Criando uma ArrayList chamada `imagePaths` para armazenar os caminhos dos arquivos de imagem.
     
   * `String[] projection = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA};`:
      O array `projection` especifica quais colunas devem ser recuperadas do `MediaStore`. No caso, são o `ID` e o caminho do arquivo ( `DATA` ) da imagem.

   * `String sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC";`:
      Define a ordenação dos resultados da consulta pelo campo `DATE_ADDED` em ordem decrescente, ou seja, as imagens mais recentes primeiro.

  * `try (Cursor cursor = context.getContentResolver().query( MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, sortOrder)){`:
    
      - O bloco try-with-resources é usado para abrir o cursor e garantir seu fechamento automático quando não for mais necessário.
      - `getContentResolver()` é utilizado para obter o resolvedor de conteúdo do contexto.
      - `query()` é chamado para executar a consulta no`MediaStore`, utilizando a `URI` das imagens na memória externa, as colunas definidas em projection, sem cláusula `WHERE` ( `null` ), sem argumentos de seleção ( `null` ), e com a ordenação definida.

   * `if (cursor != null && cursor.moveToFirst()) { int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
      do {
            String imagePath = cursor.getString(dataColumnIndex);
            imagePaths.add(imagePath);
        } while (cursor.moveToNext());
    }`:
    - Verifica se o cursor não está vazio e move para o primeiro resultado.
    - Obtém o índice da coluna que contém os caminhos dos arquivos de imagem.
    - Itera sobre os resultados do cursor, obtendo o caminho do arquivo de imagem e adicionando-o à `ArrayList`.

  * `return imagePaths;`:
      Retorna a `ArrayList` contendo os caminhos dos arquivos de imagem.
 <br>


```java

    private ArrayList<String> getImagesFromMediaStore(Context context) {
        ArrayList<String> imagePaths = new ArrayList<>();

        String[] projection = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA};

        String sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC";

        try (Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                sortOrder
        )) {
            if (cursor != null && cursor.moveToFirst()) {
                int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);

                do {
                    String imagePath = cursor.getString(dataColumnIndex);
                    imagePaths.add(imagePath);
                } while (cursor.moveToNext());
            }
        }

        return imagePaths;
    }


```

 <br>
 

Agora, ainda no arquivo (`MainActivity.java`),  declarada uma variável do tipo GridView, identifique o elemento GridView do layout( `activity_main.xml` ) e configure um adaptador personalizado (CustomAdapter) para a GridView. 

* Declaração de atributo: `GridView gridView;`.
* Identificando o elemento : `GridView = findViewById(R.id.GridView);`, através do id GridView que está no arquivo XML `activity_main.xml`.
* Adaptador personalizado (CustomAdapter) configurado para a GridView.: `gridView.setAdapter(new CustomAdapter(this, getImagesFromMediaStore(getApplicationContext())));`




  


  



     
