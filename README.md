# Capturando Imagens e Listando com GridView

Este repositório contém uma aplicação simples que utiliza a câmera do celular para tirar fotos. Em seguida, as fotos são listadas em uma GridView. O objetivo é fornecer uma demonstração didática das funcionalidades mencionadas para os alunos da disciplina de Laboratório de Programação para Dispositivos Móveis, facilitando a aprendizagem e a aplicação prática desses conceitos.

## Sumário
1. [Capturando imagens pela camêra](#cam)
2. [Carregando imagens da galeria](#galery)
3. [Salvando imagens](#save)
4. [Utilizando GridView](#gridview)
5. [Carregando imagens no GridView](#put-images)

   
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
## 4. Utilizando GridView
<a id="put-images"></a>
## 5. Carregando imagens no GridView
