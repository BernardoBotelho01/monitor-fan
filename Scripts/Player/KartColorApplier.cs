using UnityEngine;

public class KartColorApplier : MonoBehaviour
{
    [Header("Renderers")]
    public Renderer bodyRenderer;
    public Renderer helmetRenderer;

    [Header("Preset")]
    public KartColorPreset preset = KartColorPreset.RedBlue;

    public enum KartColorPreset
    {
        RedBlue,
        GreenYellow,
        BlueRed,
        YellowGreen
    }

    private void Start()
    {
        ApplyPreset(preset);
    }

    public void ApplyPreset(KartColorPreset selectedPreset)
    {
        Color bodyColor = Color.white;
        Color helmetColor = Color.white;

        switch (selectedPreset)
        {
            case KartColorPreset.RedBlue:
                bodyColor = HexToColor("#FF2D2D");
                helmetColor = HexToColor("#2D7DFF");
                break;

            case KartColorPreset.GreenYellow:
                bodyColor = HexToColor("#32D74B");
                helmetColor = HexToColor("#FFD60A");
                break;

            case KartColorPreset.BlueRed:
                bodyColor = HexToColor("#1E6CFF");
                helmetColor = HexToColor("#FF3B30");
                break;

            case KartColorPreset.YellowGreen:
                bodyColor = HexToColor("#FFD60A");
                helmetColor = HexToColor("#32D74B");
                break;
        }

        SetRendererColor(bodyRenderer, bodyColor, 0.6f);
        SetRendererColor(helmetRenderer, helmetColor, 0.85f);
    }

    private void SetRendererColor(Renderer rend, Color color, float smoothness)
    {
        if (rend == null) return;

        Material mat = rend.material;
        Shader urpLit = Shader.Find("Universal Render Pipeline/Lit");
        if (urpLit != null)
            mat.shader = urpLit;

        if (mat.HasProperty("_BaseColor"))
            mat.SetColor("_BaseColor", color);

        if (mat.HasProperty("_Smoothness"))
            mat.SetFloat("_Smoothness", smoothness);

        if (mat.HasProperty("_Metallic"))
            mat.SetFloat("_Metallic", 0f);
    }

    private Color HexToColor(string hex)
    {
        if (ColorUtility.TryParseHtmlString(hex, out Color color))
            return color;

        return Color.white;
    }
}
