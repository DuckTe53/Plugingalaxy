# GalaxyStats

Paper eklentisi: oyuncu istatistiklerini (olum, oldurme, oynama suresi) her 5 dakikada bir Firebase Realtime Database'e yazar.

## Derleme
1. Java 21 ve Maven kur.
2. Proje klasorunde: `mvn package`
3. `target/GalaxyStats-1.0.jar` dosyasini sunucunun `plugins` klasorune koy.

## Ayar
`plugins/GalaxyStats/config.yml` icindeki `firebase-secret` degerini Firebase Console > Project settings > Service accounts > Database secrets anahtariyla degistir. Anahtari GitHub'a koyma.

## Notlar
- pom.xml icindeki paper-api surumu (26.2-R0.1-SNAPSHOT) dogrulanmamistir. Maven "bulunamadi" derse papermc.io'dan guncel koordinati kullan.
- Statistic.PLAY_ONE_MINUTE derleme hatasi verirse Paper surumune gore alan adini kontrol et.
